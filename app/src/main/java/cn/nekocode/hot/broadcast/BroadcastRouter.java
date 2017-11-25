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

package cn.nekocode.hot.broadcast;

import android.content.Context;
import android.os.Parcelable;

import java.util.ArrayList;

import cn.nekocode.hot.BuildConfig;
import cn.nekocode.hot.Constants;
import cn.nekocode.hot.data.model.Column;
import cn.nekocode.hot.ui.browser.BrowserActivity;
import cn.nekocode.hot.ui.setting.ColumnConfigActivity;
import cn.nekocode.meepo.Meepo;
import cn.nekocode.meepo.annotation.Bundle;
import cn.nekocode.meepo.annotation.Query;
import cn.nekocode.meepo.annotation.TargetAction;
import cn.nekocode.meepo.annotation.TargetClass;
import cn.nekocode.meepo.annotation.TargetPath;
import cn.nekocode.meepo.config.UriConfig;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public interface BroadcastRouter {
    BroadcastRouter IMPL = new Meepo.Builder()
            .config(new BroadcastConfig()).build().create(BroadcastRouter.class);


    @TargetAction(Constants.ACTION_NOTIFY_COLUMN_INSTALLED)
    boolean tellColumnInstalled(Context context, @Bundle(Constants.ARG_COLUMNS) ArrayList<Parcelable> columns);

    @TargetAction(Constants.ACTION_NOTIFY_COLUMN_UNINSTALLED)
    boolean tellColumnUninstalled(Context context, @Bundle(Constants.ARG_COLUMNS) ArrayList<Parcelable> columns);

    @TargetAction(Constants.ACTION_NOTIFY_COLUMN_PREFERENCE_CHANGED)
    boolean tellColumnPreferenceChanged(Context context, @Bundle(Constants.ARG_COLUMNS) ArrayList<Parcelable> columns);

    @TargetAction(Constants.ACTION_NOTIFY_COLUMN_CONFIG_CHANGED)
    boolean tellColumnConfigChanged(Context context, @Bundle(Constants.ARG_COLUMNID) String columnId);
}
