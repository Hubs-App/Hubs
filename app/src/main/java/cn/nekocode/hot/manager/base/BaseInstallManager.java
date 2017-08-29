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
import android.support.annotation.Nullable;

import java.util.UUID;

import cn.nekocode.hot.data.model.Column;

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
     * Install package by file path
     */
    @Nullable
    public abstract Column install(@NonNull String packagePath);

    /**
     * Uninstall column
     */
    public abstract boolean uninstall(@NonNull UUID columnId);

    /**
     * Check if a column is installed
     */
    public abstract boolean isInstalled(@NonNull UUID columnId);
}
