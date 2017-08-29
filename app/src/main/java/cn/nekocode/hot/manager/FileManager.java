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

package cn.nekocode.hot.manager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;

import cn.nekocode.hot.manager.base.BaseFileManager;
import cn.nekocode.hot.util.PathUtil;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class FileManager extends BaseFileManager {
    private static final String ROOT_DIRECTORY = "HotApp";
    private static final String COLUMN_DIRECTORY = "Column";


    @Override
    public boolean createBaseDirectoriesIfNotExist(@NonNull Context context) {
        File dir = PathUtil.getExternalStorageDirectory();
        if (dir == null) {
            return false;
        }

        final String rootPath = dir.getPath() + File.separator + ROOT_DIRECTORY;
        dir = new File(rootPath);
        if ((!dir.exists()) && (!dir.mkdir())) {
            return false;
        }

        dir = new File(rootPath + File.separator + COLUMN_DIRECTORY);
        if ((!dir.exists()) && (!dir.mkdir())) {
            return false;
        }

        return true;
    }

    @Override
    @Nullable
    public File getRootDirectory() {
        File dir = PathUtil.getExternalStorageDirectory();
        if (dir != null) {
            dir = new File(dir.getPath() + File.separator + ROOT_DIRECTORY);
            return dir.exists() ? dir : null;
        }
        return null;
    }

    @Override
    @Nullable
    public File getColumnDirectory() {
        File dir = PathUtil.getExternalStorageDirectory();
        if (dir != null) {
            dir = new File(dir.getPath() + File.separator + COLUMN_DIRECTORY);
            return dir.exists() ? dir : null;
        }
        return null;
    }
}
