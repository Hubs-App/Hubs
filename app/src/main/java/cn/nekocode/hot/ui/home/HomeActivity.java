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
import android.support.v4.app.Fragment;

import com.evernote.android.state.State;
import com.evernote.android.state.StateSaver;

import java.util.UUID;

import cn.nekocode.hot.R;
import cn.nekocode.hot.base.BaseActivity;
import cn.nekocode.hot.data.model.Column;
import cn.nekocode.hot.databinding.ActivityHomeBinding;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class HomeActivity extends BaseActivity {
    private ActivityHomeBinding mBinding;

    @State
    public Column[] mColumns;
    private Fragment[] mColumnFragments;
    private ColumnPagerAdapter mPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StateSaver.restoreInstanceState(this, savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        // Mock data
        if (mColumns == null) {
            mColumns = new Column[4];
            for (int i = 0; i < 4; i++) {
                mColumns[i] = new Column();
                mColumns[i].setId(UUID.randomUUID());
                mColumns[i].setName("Column" + i);
                mColumns[i].setType(Column.TYPE_ARTICLE);
            }
        }

        setSupportActionBar(mBinding.toolbar);

        mPagerAdapter = new ColumnPagerAdapter(getSupportFragmentManager(), mColumns);
        mBinding.viewPager.setAdapter(mPagerAdapter);
        mBinding.tabs.setupWithViewPager(mBinding.viewPager);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        StateSaver.saveInstanceState(this, outState);
    }
}
