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

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.UUID;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public abstract class BaseFileManager {

    /**
     * Create some base directories
     */
    public abstract boolean createBaseDirectoriesIfNotExist(@NonNull Context context);

    /**
     * Get path of root directory
     */
    @Nullable
    public abstract File getRootDirectory();

    /**
     * Get path of columns' parent directory
     */
    @Nullable
    public abstract File getColumnsDirectory();

    /**
     * Get path of column directory
     */
    @Nullable
    public abstract File getColumnDirectory(@NonNull UUID columnId);
}
