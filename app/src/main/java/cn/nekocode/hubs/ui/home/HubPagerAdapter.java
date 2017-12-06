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

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;

import java.util.ArrayList;

import cn.nekocode.hubs.base.BaseHubFragment;
import cn.nekocode.hubs.data.model.Hub;
import cn.nekocode.hubs.util.ExFragmentPagerAdapter;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class HubPagerAdapter extends ExFragmentPagerAdapter<Hub> {


    public HubPagerAdapter(@NonNull FragmentManager fm, @NonNull ArrayList<Hub> hubs) {
        super(fm, hubs);
    }

    @Override
    public Fragment getItem(int position) {
        return BaseHubFragment.newInstance(mList.get(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mList.get(position).getName();
    }

    @Override
    public long getItemId(Hub data) {
        return data.hashCode();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if (object instanceof BaseHubFragment) {
            ((BaseHubFragment) object).tryFirstLoad();
        }
    }
}
