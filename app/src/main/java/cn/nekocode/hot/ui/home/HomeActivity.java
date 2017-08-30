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

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.evernote.android.state.State;
import com.evernote.android.state.StateSaver;

import java.util.ArrayList;

import cn.nekocode.hot.ActivityRouter;
import cn.nekocode.hot.HotApplication;
import cn.nekocode.hot.R;
import cn.nekocode.hot.base.BaseActivity;
import cn.nekocode.hot.data.model.Column;
import cn.nekocode.hot.databinding.ActivityHomeBinding;
import cn.nekocode.hot.manager.base.BaseFileManager;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class HomeActivity extends BaseActivity {
    private ActivityHomeBinding mBinding;

    @State
    public ArrayList<Column> mColumns;
    private ColumnPagerAdapter mPagerAdapter;
    private BaseFileManager mFileManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StateSaver.restoreInstanceState(this, savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        /*
          Create base directories
         */
        mFileManager = HotApplication.getDefaultFileManager(this);
        if (!mFileManager.createBaseDirectoriesIfNotExist(this)) {
            Toast.makeText(this, R.string.toast_create_directories_failed, Toast.LENGTH_SHORT).show();

        } else {
            if (mColumns == null) {
                mColumns = new ArrayList<>();

                // todo: load columns from files
            }

            setSupportActionBar(mBinding.toolbar);

            mPagerAdapter = new ColumnPagerAdapter(getSupportFragmentManager(), mColumns);
            mBinding.viewPager.setAdapter(mPagerAdapter);
            mBinding.tabs.setupWithViewPager(mBinding.viewPager);
        }
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
