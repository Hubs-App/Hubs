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

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import cn.nekocode.hot.base.BaseColumnFragment;
import cn.nekocode.hot.data.model.Column;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class ColumnPagerAdapter extends FragmentPagerAdapter {
    private FragmentManager mFragmentManager;

    private Column[] mColumns;
    private Fragment[] mFragments;


    public ColumnPagerAdapter(@NonNull FragmentManager fm, @NonNull Column[] columns) {
        super(fm);
        this.mFragmentManager = fm;
        this.mColumns = columns;
        this.mFragments = new Fragment[columns.length];
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
//        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
//
//        // Do we already have this fragment?
//        final Column column = mColumns[position];
//        final String tag = column.getId().toString();
//        Fragment fragment = mFragmentManager.findFragmentByTag(tag);
//        if (fragment != null && !fragment.isDetached()) {
//            fragmentTransaction.detach(fragment);
//        }
//
//        fragment = getItem(position);
//        fragmentTransaction.add(container.getId(), fragment, tag);
//        fragmentTransaction.commit();

        return super.instantiateItem(container, position);
    }

    @Override
    public Fragment getItem(int position) {
        final Fragment fragment = BaseColumnFragment.newInstance(mColumns[position]);
        mFragments[position] = fragment;
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mColumns[position].getName();
    }

    @Override
    public int getCount() {
        return mColumns.length;
    }
}
