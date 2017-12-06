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

package cn.nekocode.hubs;

import android.content.Context;

import cn.nekocode.hubs.data.model.Column;
import cn.nekocode.hubs.ui.browser.BrowserActivity;
import cn.nekocode.hubs.ui.setting.ColumnConfigActivity;
import cn.nekocode.meepo.Meepo;
import cn.nekocode.meepo.annotation.Bundle;
import cn.nekocode.meepo.annotation.Query;
import cn.nekocode.meepo.annotation.TargetClass;
import cn.nekocode.meepo.annotation.TargetPath;
import cn.nekocode.meepo.config.UriConfig;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public interface ActivityRouter {
    ActivityRouter IMPL = new Meepo.Builder()
            .config(new UriConfig().scheme(BuildConfig.SCHEME).host(BuildConfig.APPLICATION_ID))
            .build().create(ActivityRouter.class);


    @TargetPath("browser")
    boolean gotoBrowser(Context context, @Query("url") String url);

    @TargetPath("browser")
    boolean gotoBrowser(Context context, @Query("column_id") String columnId, @Query("url") String url);

    @TargetClass(BrowserActivity.class)
    boolean gotoBrowser(Context context, @Bundle("column") Column column, @Bundle("url") String url);

    @TargetPath("setting")
    boolean gotoSetting(Context context);

    @TargetPath("column_manager")
    boolean gotoColumnManager(Context context);

    @TargetClass(ColumnConfigActivity.class)
    boolean gotoColumnConfig(Context context, @Bundle("column") Column column);
}
