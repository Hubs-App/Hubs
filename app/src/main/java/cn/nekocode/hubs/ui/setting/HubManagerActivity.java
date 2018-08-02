/*
 * Copyright (C) 2017 nekocode (nekocode.cn@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cn.nekocode.hubs.ui.setting;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.widget.Toast;

import com.evernote.android.state.State;
import com.evernote.android.state.StateSaver;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.util.ArrayList;

import cn.nekocode.hubs.ActivityRouter;
import cn.nekocode.hubs.Constants;
import cn.nekocode.hubs.HubsApplication;
import cn.nekocode.hubs.R;
import cn.nekocode.hubs.base.BaseActivity;
import cn.nekocode.hubs.broadcast.BroadcastRouter;
import cn.nekocode.hubs.data.model.Hub;
import cn.nekocode.hubs.data.model.HubPreference;
import cn.nekocode.hubs.databinding.ActivityHubMangerBinding;
import cn.nekocode.hubs.manager.base.BaseHubManager;
import cn.nekocode.hubs.manager.base.BasePreferenceManager;
import cn.nekocode.hubs.util.HubUtil;
import cn.nekocode.hubs.util.CommonUtil;
import cn.nekocode.hubs.util.DividerItemDecoration;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class HubManagerActivity extends BaseActivity implements HubListAdapter.UIEventListener {
    private ActivityHubMangerBinding mBinding;
    @State
    public ArrayList<HubPreference> mPreferences;
    private BaseHubManager mHubManager;
    private BasePreferenceManager mPreferenceManager;
    private HubListAdapter mAdapter;
    @State
    public boolean mIsPreferenceChanged = false;
    private final BroadcastReceiver mBroadcastReceiver = new HubManagerBroadcastReceiver();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StateSaver.restoreInstanceState(this, savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_hub_manger);
        mHubManager = HubsApplication.getDefaultHubManager(this);
        mPreferenceManager = HubsApplication.getDefaultPreferenceManager(this);

        /*
          Data initialize
         */
        if (mPreferences == null) {
            mPreferences = new ArrayList<>();
            loadHubPreferences();
        }


        /*
          View initialize
         */
        setSupportActionBar(mBinding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Setup the recyclerview
        mAdapter = new HubListAdapter(mPreferences);
        mAdapter.setUIEventListener(this);

        mBinding.recyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBinding.recyclerView.setAdapter(mAdapter);
        mBinding.recyclerView.setItemAnimator(null);
        mBinding.recyclerView.addItemDecoration(DividerItemDecoration.obtainDefault(this));


        /*
          Register broadcast receiver
         */
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_NOTIFY_HUB_INSTALLED);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mBroadcastReceiver, intentFilter);
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        StateSaver.saveInstanceState(this, outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mBroadcastReceiver);
        unregisterReceiver(mBroadcastReceiver);

        if (mIsPreferenceChanged) {
            final ArrayList<Hub> orderedHub = new ArrayList<>();
            for (HubPreference preference : mPreferences) {
                if (preference.isVisible()) {
                    orderedHub.add(preference.getHub());
                }
            }

            // Send local broadcast
            BroadcastRouter.IMPL.tellHubPreferenceChanged(this, orderedHub);
        }
    }

    @Override
    public void onItemsSwapped() {
        mPreferenceManager.saveHubPreferences(mPreferences);
        mIsPreferenceChanged = true;
    }

    @Override
    public void onItemConfigButtonClick(int position, HubPreference preference) {
        ActivityRouter.IMPL.gotoHubConfig(this, preference.getHub());
    }

    @Override
    public void onItemVisibilityButtonClick(int position, HubPreference preference) {
        mPreferenceManager.updateHubPreferences(preference);
        mIsPreferenceChanged = true;
    }

    @Override
    public void onItemUninstallButtonClick(int position, HubPreference preference) {
        showUninstallDialog(preference.getHub(), () -> {
            // When uninstall success
            mPreferences.remove(position);
            mPreferenceManager.removeHubPreferences(preference);
            mAdapter.notifyItemRemoved(position);
            mIsPreferenceChanged = true;
        });
    }

    private void loadHubPreferences() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        mHubManager.getAllInstalled()
                .subscribeOn(Schedulers.io())
                .flatMap(hubs -> mPreferenceManager.loadHubPreferences(hubs))
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(preferences -> {
                    progressDialog.dismiss();
                    mPreferences.clear();
                    mPreferences.addAll(preferences);
                    mAdapter.notifyDataSetChanged();

                }, throwable -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, R.string.toast_load_hubs_failed, Toast.LENGTH_SHORT).show();
                });
    }

    private void showUninstallDialog(Hub hub, final Runnable successCallback) {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.dialog_ensure_uninstall_hub, hub.getName(), hub.getVersion()))
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    uninstallHub(hub, successCallback);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void uninstallHub(final Hub hub, final Runnable successCallback) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.dialog_uninstalling_hub));
        progressDialog.setCancelable(false);
        progressDialog.show();

        mHubManager.uninstall(hub.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(isSuccess -> {
                    if (!isSuccess) {
                        progressDialog.dismiss();
                        Toast.makeText(this, R.string.toast_uninstall_hub_failed, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    successCallback.run();
                    progressDialog.dismiss();
                    Toast.makeText(this, R.string.toast_uninstall_hub_success, Toast.LENGTH_SHORT).show();

                    // Send local broadcast
                    BroadcastRouter.IMPL.tellHubUninstalled(this, CommonUtil.toArrayList(hub));

                }, throwable -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, R.string.toast_uninstall_hub_failed, Toast.LENGTH_SHORT).show();
                });
    }


    private class HubManagerBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action == null) return;

            String hubId;
            int index;
            switch (action) {
                case Constants.ACTION_NOTIFY_HUB_INSTALLED:
                    hubId = intent.getStringExtra(Constants.ARG_HUB_ID);
                    if (hubId == null) return;

                    index = HubUtil.indexOfHubPreference(mPreferences, hubId);
                    if (index < 0) {
                        loadHubPreferences();

                    } else {
                        mHubManager.readConfig(hubId)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(HubManagerActivity.this)))
                                .subscribe(hub2 -> {
                                    mPreferences.get(index).setHub(hub2);
                                    mAdapter.notifyItemChanged(index);

                                }, throwable -> {});
                    }
                    break;
            }
        }
    }
}
