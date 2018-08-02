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

package cn.nekocode.hubs.ui.home;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.evernote.android.state.State;
import com.evernote.android.state.StateSaver;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.io.File;
import java.util.ArrayList;

import cn.nekocode.hubs.ActivityRouter;
import cn.nekocode.hubs.BuildConfig;
import cn.nekocode.hubs.Constants;
import cn.nekocode.hubs.HubsApplication;
import cn.nekocode.hubs.R;
import cn.nekocode.hubs.base.BaseActivity;
import cn.nekocode.hubs.broadcast.BroadcastRouter;
import cn.nekocode.hubs.data.model.Hub;
import cn.nekocode.hubs.databinding.ActivityHomeBinding;
import cn.nekocode.hubs.manager.base.BaseHubManager;
import cn.nekocode.hubs.manager.base.BaseFileManager;
import cn.nekocode.hubs.manager.base.BasePreferenceManager;
import cn.nekocode.hubs.util.HubUtil;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class HomeActivity extends BaseActivity {
    private ActivityHomeBinding mBinding;

    @State
    public ArrayList<Hub> mHubs;
    private HubPagerAdapter mPagerAdapter;
    private BaseFileManager mFileManager;
    private BaseHubManager mHubManager;
    private BasePreferenceManager mPreferenceManager;
    private final BroadcastReceiver mBroadcastReceiver = new HomeBroadcastReceiver();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StateSaver.restoreInstanceState(this, savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        mFileManager = HubsApplication.getDefaultFileManager(this);
        mHubManager = HubsApplication.getDefaultHubManager(this);
        mPreferenceManager = HubsApplication.getDefaultPreferenceManager(this);

        /*
          Create base directories
         */
        if (!mFileManager.createBaseDirectoriesIfNotExist()) {
            Toast.makeText(this, R.string.toast_create_directories_failed, Toast.LENGTH_SHORT).show();
            return;
        }


        if (mHubs == null) {
            mHubs = new ArrayList<>();
            loadHubs();
        }

        setSupportActionBar(mBinding.toolbar);

        mPagerAdapter = new HubPagerAdapter(getSupportFragmentManager(), mHubs);
        mBinding.viewPager.setAdapter(mPagerAdapter);
        mBinding.tabs.setupWithViewPager(mBinding.viewPager);

        checkIfNeddToInstall(getIntent());

        /*
          Register broadcast receiver
         */
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_NOTIFY_HUB_INSTALLED);
        intentFilter.addAction(Constants.ACTION_NOTIFY_HUB_UNINSTALLED);
        intentFilter.addAction(Constants.ACTION_NOTIFY_HUB_PREFERENCE_CHANGED);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mBroadcastReceiver, intentFilter);
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkIfNeddToInstall(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mBroadcastReceiver);
        unregisterReceiver(mBroadcastReceiver);
    }

    private void loadHubs() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        mHubManager.getAllInstalled()
                .subscribeOn(Schedulers.io())
                .flatMap(hubs -> mPreferenceManager.getOrderedVisibleHubs(hubs))
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(hubs -> {
                    progressDialog.dismiss();
                    mHubs.clear();
                    mHubs.addAll(hubs);
                    mPagerAdapter.notifyDataSetChanged();

                }, throwable -> {
                    progressDialog.dismiss();
                    Toast.makeText(HomeActivity.this, R.string.toast_load_hubs_failed, Toast.LENGTH_SHORT).show();
                });
    }

    private void checkIfNeddToInstall(Intent intent) {
        if (!Intent.ACTION_VIEW.equals(intent.getAction()) ||
                intent.getData() == null || BuildConfig.SCHEME.equalsIgnoreCase(intent.getScheme())) {

            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.dialog_checking_hub));
        progressDialog.setCancelable(false);
        progressDialog.show();

        mFileManager.getFile(intent.getData())
                .flatMap(file ->
                        mHubManager.readConfig(file)
                                .map(hub -> Pair.create(file, hub))
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(pair -> {
                    final File hubPackgeFile = pair.first;
                    final Hub hub = pair.second;

                    if (!mHubManager.isInstalled(hub.getId())) {
                        showInstallDialog(hubPackgeFile, hub, progressDialog);
                    } else {
                        showReinstallDialog(hubPackgeFile, hub, progressDialog);
                    }
                }, throwable -> {
                    progressDialog.dismiss();
                    Toast.makeText(HomeActivity.this, R.string.toast_install_hub_failed, Toast.LENGTH_SHORT).show();
                });
    }

    private void showInstallDialog(File hubPackageFile, Hub hub, final ProgressDialog progressDialog) {
        progressDialog.dismiss();
        new AlertDialog.Builder(HomeActivity.this)
                .setMessage(getString(R.string.dialog_ensure_install_hub,
                        hub.getName(), hub.getVersion()))
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    installHub(hubPackageFile, progressDialog);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showReinstallDialog(File hubPackageFile, Hub hub, final ProgressDialog progressDialog) {
        mHubManager.readConfig(hub.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(installedHub -> {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(HomeActivity.this)
                            .setMessage(getString(R.string.dialog_ensure_reinstall_hub,
                                    installedHub.getName(), installedHub.getVersion(), hub.getVersion()))
                            .setPositiveButton(R.string.yes, (dialog, which) -> {
                                installHub(hubPackageFile, progressDialog);
                            })
                            .setNegativeButton(R.string.cancel, null)
                            .show();

                }, throwable -> {
                    progressDialog.dismiss();
                    Toast.makeText(HomeActivity.this, R.string.toast_install_hub_failed, Toast.LENGTH_SHORT).show();
                });
    }

    private void installHub(File hubPackageFile, final ProgressDialog progressDialog) {
        progressDialog.setMessage(getString(R.string.dialog_installing_hub));
        progressDialog.setCancelable(false);
        progressDialog.show();

        mHubManager.install(this, hubPackageFile)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(hub -> {
                    progressDialog.dismiss();
                    Toast.makeText(HomeActivity.this, R.string.toast_install_hub_success, Toast.LENGTH_SHORT).show();

                    // Send local broadcast
                    BroadcastRouter.IMPL.tellHubInstalled(this, hub);

                }, throwable -> {
                    progressDialog.dismiss();
                    Toast.makeText(HomeActivity.this, R.string.toast_install_hub_failed, Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        StateSaver.saveInstanceState(this, outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSetting:
                ActivityRouter.IMPL.gotoSetting(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private class HomeBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action == null) return;

            ArrayList<Hub> hubs;
            Hub hub;
            String hubId;
            int index;
            switch (action) {
                case Constants.ACTION_NOTIFY_HUB_INSTALLED:
                    hub = intent.getParcelableExtra(Constants.ARG_HUB);
                    hubId = intent.getStringExtra(Constants.ARG_HUB_ID);
                    if (hub == null && hubId == null) break;
                    if (hubId == null) hubId = hub.getId();

                    index = HubUtil.indexOfHub(mHubs, hubId);

                    (hub != null ? Single.just(hub) :
                            mHubManager.readConfig(hubId).subscribeOn(Schedulers.io()))
                            .flatMap(_hub -> {
                                if (index < 0) {
                                    // If not found in visible hub list
                                    return mPreferenceManager.loadHubPreference(_hub)
                                            .subscribeOn(Schedulers.io())
                                            .flatMap(hubPreference -> {
                                                if (!hubPreference.isVisible()) {
                                                    return Single.never();
                                                } else {
                                                    return Single.just(_hub);
                                                }
                                            });

                                } else {
                                    return Single.just(_hub);
                                }
                            })
                            .observeOn(AndroidSchedulers.mainThread())
                            .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(HomeActivity.this)))
                            .subscribe(_hub -> {
                                if (index < 0) {
                                    // Install
                                    mHubs.add(_hub);

                                } else {
                                    // Reinstall
                                    mHubs.set(index, _hub);
                                    mPagerAdapter.hackRecreateFragment(index);
                                }
                                mPagerAdapter.notifyDataSetChanged();

                            }, throwable -> {});
                    break;

                case Constants.ACTION_NOTIFY_HUB_UNINSTALLED:
                    hubs = intent.getParcelableArrayListExtra(Constants.ARG_HUBS);
                    if (hubs == null) break;

                    mHubs.removeAll(hubs);
                    mPagerAdapter.notifyDataSetChanged();
                    break;

                case Constants.ACTION_NOTIFY_HUB_PREFERENCE_CHANGED:
                    hubs = intent.getParcelableArrayListExtra(Constants.ARG_HUBS);
                    if (hubs == null) break;

                    mHubs.clear();
                    mHubs.addAll(hubs);
                    mPagerAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }
}
