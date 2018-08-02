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

import cn.nekocode.hubs.data.model.Hub;
import cn.nekocode.hubs.ui.browser.BrowserActivity;
import cn.nekocode.hubs.ui.setting.HubConfigActivity;
import cn.nekocode.meepo.Meepo;
import cn.nekocode.meepo.annotation.BundleParam;
import cn.nekocode.meepo.annotation.Clazz;
import cn.nekocode.meepo.annotation.Path;
import cn.nekocode.meepo.annotation.QueryParam;
import cn.nekocode.meepo.config.UriConfig;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public interface ActivityRouter {
    ActivityRouter IMPL = new Meepo.Builder()
            .config(new UriConfig().scheme(BuildConfig.SCHEME).host(BuildConfig.APPLICATION_ID))
            .build().create(ActivityRouter.class);


    @Path("browser")
    boolean gotoBrowser(Context context, @QueryParam("url") String url);

    @Path("browser")
    boolean gotoBrowser(Context context, @QueryParam("hub_id") String hubId, @QueryParam("url") String url);

    @Clazz(BrowserActivity.class)
    boolean gotoBrowser(Context context, @BundleParam("hub") Hub hub, @BundleParam("url") String url);

    @Path("setting")
    boolean gotoSetting(Context context);

    @Path("hub_manager")
    boolean gotoHubManager(Context context);

    @Clazz(HubConfigActivity.class)
    boolean gotoHubConfig(Context context, @BundleParam("hub") Hub hub);
}
