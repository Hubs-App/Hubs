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

import cn.nekocode.hubs.data.model.Column;
import cn.nekocode.hubs.data.model.UserConfig;
import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public abstract class BaseColumnManager {
    private final BaseFileManager mFileManager;


    public BaseColumnManager(BaseFileManager fileManager) {
        this.mFileManager = fileManager;
    }

    public BaseFileManager getFileManager() {
        return mFileManager;
    }

    /**
     * Read config form a column package
     */
    @NonNull
    public abstract Single<Column> readConfig(@NonNull File packageFile);

    /**
     * Read config form a installed column
     */
    @NonNull
    public abstract Single<Column> readConfig(@NonNull String columnId);

    /**
     * Write user config to file
     */
    public abstract Completable writeUserConfig(@NonNull String columnId, @NonNull UserConfig config);

    /**
     * Install column
     */
    @NonNull
    public abstract Single<Column> install(@NonNull Context context, @NonNull File packageFile);

    /**
     * Uninstall column
     */
    @NonNull
    public abstract Single<Boolean> uninstall(@NonNull String columnId);

    /**
     * Check if a column is installed
     */
    public abstract boolean isInstalled(@NonNull String columnId);

    /**
     * Get all installed columns
     */
    @NonNull
    public abstract Single<List<Column>> getAllInstalled();
}
