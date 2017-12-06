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

package cn.nekocode.hubs.manager.base;

import android.support.annotation.NonNull;

import java.util.List;

import cn.nekocode.hubs.data.model.Hub;
import cn.nekocode.hubs.data.model.HubPreference;
import io.reactivex.Single;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public abstract class BasePreferenceManager {

    /**
     * Get ordered visible hubs
     */
    @NonNull
    public abstract Single<List<Hub>> getOrderedVisibleHubs(@NonNull List<Hub> hubs);

    /**
     * Load hub preference objects
     */
    @NonNull
    public abstract Single<List<HubPreference>> loadHubPreferences(@NonNull List<Hub> hubs);

    /**
     * Load hub preference object
     */
    @NonNull
    public abstract Single<HubPreference> loadHubPreference(@NonNull Hub hub);

    /**
     * Update hub preference objects
     */
    public abstract void updateHubPreferences(@NonNull HubPreference... preferences);

    /**
     * Save hub preference objects
     */
    public abstract void saveHubPreferences(@NonNull List<HubPreference> preferences);

    /**
     * Remove hub preference objects
     */
    public abstract void removeHubPreferences(@NonNull HubPreference... preferences);

    /**
     * Remove all hub preference objects
     */
    public abstract void removeAllHubPreferences();
}
