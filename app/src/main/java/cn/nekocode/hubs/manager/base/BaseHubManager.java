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

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.List;

import cn.nekocode.hubs.data.model.Hub;
import cn.nekocode.hubs.data.model.UserConfig;
import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public abstract class BaseHubManager {
    private final BaseFileManager mFileManager;


    public BaseHubManager(BaseFileManager fileManager) {
        this.mFileManager = fileManager;
    }

    public BaseFileManager getFileManager() {
        return mFileManager;
    }

    /**
     * Read config form a hub package
     */
    @NonNull
    public abstract Single<Hub> readConfig(@NonNull File packageFile);

    /**
     * Read config form a installed hub
     */
    @NonNull
    public abstract Single<Hub> readConfig(@NonNull String hubId);

    /**
     * Write user config to file
     */
    public abstract Completable writeUserConfig(@NonNull String hubId, @NonNull UserConfig config);

    /**
     * Install hub
     */
    @NonNull
    public abstract Single<Hub> install(@NonNull Context context, @NonNull File packageFile);

    /**
     * Uninstall hub
     */
    @NonNull
    public abstract Single<Boolean> uninstall(@NonNull String hubId);

    /**
     * Check if a hub is installed
     */
    public abstract boolean isInstalled(@NonNull String hubId);

    /**
     * Get all installed hubs
     */
    @NonNull
    public abstract Single<List<Hub>> getAllInstalled();
}
