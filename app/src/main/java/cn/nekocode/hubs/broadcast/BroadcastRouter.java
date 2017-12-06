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

package cn.nekocode.hubs.broadcast;

import android.content.Context;

import java.util.ArrayList;

import cn.nekocode.hubs.Constants;
import cn.nekocode.hubs.data.model.Hub;
import cn.nekocode.meepo.Meepo;
import cn.nekocode.meepo.annotation.Bundle;
import cn.nekocode.meepo.annotation.TargetAction;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public interface BroadcastRouter {
    BroadcastRouter IMPL = new Meepo.Builder()
            .config(new BroadcastConfig()).adapter(new BroadcastCallAdapter())
            .build().create(BroadcastRouter.class);


    @TargetAction(Constants.ACTION_NOTIFY_HUB_INSTALLED)
    void tellHubInstalled(Context context, @Bundle(Constants.ARG_HUB_ID) String hubId);

    @TargetAction(Constants.ACTION_NOTIFY_HUB_INSTALLED)
    void tellHubInstalled(Context context, @Bundle(Constants.ARG_HUB) Hub hub);

    @TargetAction(Constants.ACTION_NOTIFY_HUB_UNINSTALLED)
    void tellHubUninstalled(Context context, @Bundle(Constants.ARG_HUBS) ArrayList<Hub> hubs);

    @TargetAction(Constants.ACTION_NOTIFY_HUB_PREFERENCE_CHANGED)
    void tellHubPreferenceChanged(Context context, @Bundle(Constants.ARG_HUBS) ArrayList<Hub> hubs);

}
