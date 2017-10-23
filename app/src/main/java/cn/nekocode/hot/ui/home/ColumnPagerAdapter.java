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
import android.view.ViewGroup;

import java.util.ArrayList;

import cn.nekocode.hot.base.BaseColumnFragment;
import cn.nekocode.hot.util.ExFragmentPagerAdapter;
import cn.nekocode.hot.data.model.Column;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class ColumnPagerAdapter extends ExFragmentPagerAdapter<Column> {


    public ColumnPagerAdapter(@NonNull FragmentManager fm, @NonNull ArrayList<Column> columns) {
        super(fm, columns);
    }

    @Override
    public Fragment getItem(int position) {
        return BaseColumnFragment.newInstance(mList.get(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mList.get(position).getName();
    }

    @Override
    public long getItemIdByData(Column data) {
        return data.getId().hashCode();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if (object instanceof BaseColumnFragment) {
            ((BaseColumnFragment) object).tryFirstLoad();
        }
    }
}
