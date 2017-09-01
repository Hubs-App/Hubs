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

package cn.nekocode.hot.ui.home;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.evernote.android.state.State;
import com.evernote.android.state.StateSaver;

import java.io.File;
import java.util.ArrayList;

import cn.nekocode.hot.ActivityRouter;
import cn.nekocode.hot.HotApplication;
import cn.nekocode.hot.R;
import cn.nekocode.hot.base.BaseActivity;
import cn.nekocode.hot.data.model.Column;
import cn.nekocode.hot.databinding.ActivityHomeBinding;
import cn.nekocode.hot.manager.base.BaseFileManager;
import cn.nekocode.hot.manager.base.BaseColumnManager;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class HomeActivity extends BaseActivity {
    private ActivityHomeBinding mBinding;
    private ProgressDialog mProgressDialog;

    @State
    public ArrayList<Column> mColumns;
    private ColumnPagerAdapter mPagerAdapter;
    private BaseFileManager mFileManager;
    private BaseColumnManager mColumnManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StateSaver.restoreInstanceState(this, savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        mFileManager = HotApplication.getDefaultFileManager(this);
        mColumnManager = HotApplication.getDefaultColumnManager(this);

        /*
          Create base directories
         */
        if (!mFileManager.createBaseDirectoriesIfNotExist(this)) {
            Toast.makeText(this, R.string.toast_create_directories_failed, Toast.LENGTH_SHORT).show();
            return;
        }


        if (mColumns == null) {
            mColumns = new ArrayList<>();

            // todo: load columns from files
        }

        setSupportActionBar(mBinding.toolbar);
        mProgressDialog = new ProgressDialog(this);

        mPagerAdapter = new ColumnPagerAdapter(getSupportFragmentManager(), mColumns);
        mBinding.viewPager.setAdapter(mPagerAdapter);
        mBinding.tabs.setupWithViewPager(mBinding.viewPager);

        checkInetentData(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkInetentData(intent);
    }

    private void checkInetentData(Intent intent) {
        if (intent.getData() == null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.dialog_checking_column));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        mFileManager.getFile(this, intent.getData())
                .flatMap(file ->
                        mColumnManager.readConfig(file)
                                .map(column -> Pair.create(file, column))
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(pair -> {
                    final File columnPackgeFile = pair.first;
                    final Column column = pair.second;

                    if (!mColumnManager.isInstalled(column.getId())) {
                        showInstallDialog(columnPackgeFile, column);
                    } else {
                        showReinstallDialog(columnPackgeFile, column);
                    }
                }, throwable -> {
                    mProgressDialog.dismiss();
                    Toast.makeText(HomeActivity.this, R.string.toast_install_column_failed, Toast.LENGTH_SHORT).show();
                });
    }

    private void showInstallDialog(File columnPackageFile, Column column) {
        mProgressDialog.dismiss();
        new AlertDialog.Builder(HomeActivity.this)
                .setMessage(getString(R.string.dialog_ensure_install_column,
                        column.getName(), column.getVersion()))
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    installColumn(columnPackageFile);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showReinstallDialog(File columnPackageFile, Column column) {
        mColumnManager.readConfig(column.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(installedColumn -> {
                    mProgressDialog.dismiss();
                    new AlertDialog.Builder(HomeActivity.this)
                            .setMessage(getString(R.string.dialog_ensure_reinstall_column,
                                    installedColumn.getName(), installedColumn.getVersion(), column.getVersion()))
                            .setPositiveButton(R.string.yes, (dialog, which) -> {
                                installColumn(columnPackageFile);
                            })
                            .setNegativeButton(R.string.cancel, null)
                            .show();

                }, throwable -> {
                    mProgressDialog.dismiss();
                    Toast.makeText(HomeActivity.this, R.string.toast_install_column_failed, Toast.LENGTH_SHORT).show();
                });
    }

    private void installColumn(File columnPackageFile) {
        mProgressDialog.setMessage(getString(R.string.dialog_installing_column));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        mColumnManager.install(this, columnPackageFile)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(column -> {
                    mProgressDialog.dismiss();
                    Toast.makeText(HomeActivity.this, R.string.toast_install_column_success, Toast.LENGTH_SHORT).show();

                }, throwable -> {
                    mProgressDialog.dismiss();
                    Toast.makeText(HomeActivity.this, R.string.toast_install_column_failed, Toast.LENGTH_SHORT).show();
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
}
