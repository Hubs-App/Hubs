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

package cn.nekocode.hot.util;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public abstract class ExFragmentPagerAdapter<T> extends FragmentPagerAdapter {
    private final FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction = null;
    private Fragment mCurrentPrimaryItem = null;
    private int mContainerId = View.NO_ID;

    protected ArrayList<T> mList;


    public ExFragmentPagerAdapter(@NonNull FragmentManager fm, @NonNull ArrayList<T> columns) {
        super(fm);
        this.mFragmentManager = fm;
        this.mList = columns;
    }

    @Override
    public void startUpdate(ViewGroup container) {
        final int containerId = container.getId();
        if (containerId != View.NO_ID) {
            mContainerId = container.getId();
        } else {
            throw new IllegalStateException("ViewPager with adapter " + this
                    + " requires a view id");
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }

        final String tag = makeFragmentTag(container.getId(), getItemId(position));
        Fragment fragment = mFragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            mCurTransaction.attach(fragment);
        } else {
            fragment = getItem(position);
            mCurTransaction.add(container.getId(), fragment, tag);
        }
        if (fragment != mCurrentPrimaryItem) {
            fragment.setMenuVisibility(false);
            fragment.setUserVisibleHint(false);
        }

        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }
        mCurTransaction.detach((Fragment)object);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment)object;
        if (fragment != mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                mCurrentPrimaryItem.setMenuVisibility(false);
                mCurrentPrimaryItem.setUserVisibleHint(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            mCurrentPrimaryItem = fragment;
        }
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if (mCurTransaction != null) {
            mCurTransaction.commitNowAllowingStateLoss();
            mCurTransaction = null;
        }
    }

    @Override
    public int getItemPosition(Object object) {
        if (object instanceof Fragment) {
            final Fragment fragment = (Fragment) object;
            final String tag = fragment.getTag();

            int position = 0;
            for (T data : mList) {
                if (tag.equals(makeFragmentTag(mContainerId, getItemIdByData(data)))) {
                    break;
                }
                position++;
            }

            return position;
        }

        return super.getItemPosition(object);
    }

    @Override
    public final long getItemId(int position) {
        return getItemIdByData(mList.get(position));
    }

    public abstract long getItemIdByData(T data);

    @Override
    public final int getCount() {
        return mList.size();
    }

    private static String makeFragmentTag(int viewId, long id) {
        return viewId + ":" + id;
    }
}
