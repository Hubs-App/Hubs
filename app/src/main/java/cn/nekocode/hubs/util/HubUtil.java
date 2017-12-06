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

package cn.nekocode.hubs.util;

import android.support.annotation.NonNull;

import java.util.List;

import cn.nekocode.hubs.data.model.Hub;
import cn.nekocode.hubs.data.model.HubPreference;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class HubUtil {

    public static int indexOfHub(@NonNull List<Hub> hubs, @NonNull String hubId) {
        int index = 0;
        boolean finded = false;
        for (Hub hub : hubs) {
            if (hub.getId().equalsIgnoreCase(hubId)) {
                finded = true;
                break;
            }
            index++;
        }
        return finded ? index : -1;
    }

    public static int indexOfHubPreference(@NonNull List<HubPreference> hubPreferences, @NonNull String hubId) {
        int index = 0;
        boolean finded = false;
        for (HubPreference hubPreference : hubPreferences) {
            if (hubPreference.getHubId().equalsIgnoreCase(hubId)) {
                finded = true;
                break;
            }
            index++;
        }
        return finded ? index : -1;
    }

}
