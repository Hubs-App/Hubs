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

package cn.nekocode.hot.manager.base;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.UUID;

import cn.nekocode.hot.data.model.Column;
import io.reactivex.Observable;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public abstract class BaseInstallManager {
    private final BaseFileManager mFileManager;


    public BaseInstallManager(BaseFileManager fileManager) {
        this.mFileManager = fileManager;
    }

    public BaseFileManager getFileManager() {
        return mFileManager;
    }

    /**
     * Install package
     */
    public abstract Observable<Column> readConfig(@NonNull File packageFile);

    /**
     * Install package
     */
    public abstract Observable<Column> install(@NonNull File packageFile);

    /**
     * Uninstall column
     */
    public abstract Observable<Boolean> uninstall(@NonNull UUID columnId);

    /**
     * Check if a column is installed
     */
    public abstract boolean isInstalled(@NonNull UUID columnId);
}
