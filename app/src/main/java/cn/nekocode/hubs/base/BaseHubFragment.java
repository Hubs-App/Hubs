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

package cn.nekocode.hubs.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import cn.nekocode.hubs.data.model.Hub;
import cn.nekocode.hubs.ui.hub.ArticleHubFragment;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public abstract class BaseHubFragment extends BaseLazyLoadFragment {
    public static final String ARG_HUB = "hub";

    private Hub mHub;


    @NonNull
    public static BaseHubFragment newInstance(@NonNull Hub hub) {
        final String hubType = hub.getType();
        BaseHubFragment fragment;

        switch (hubType) {
            case Hub.TYPE_ARTICLE:
            default:
                fragment = new ArticleHubFragment();
                break;
        }

        final Bundle args = new Bundle();
        args.putParcelable(ARG_HUB, hub);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHub = getArguments().getParcelable(ARG_HUB);
        if (mHub == null) throw new RuntimeException("Get hub from arguments failed.");
    }

    public Hub getHub() {
        return mHub;
    }

    public void showMessageIfInDebug(@NonNull String message) {
        if (mHub.isDebug()) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
