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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.UUID;

import cn.nekocode.hot.BuildConfig;
import cn.nekocode.hot.data.model.Column;
import cn.nekocode.hot.manager.base.BaseFileManager;
import cn.nekocode.hot.manager.base.BaseInstallManager;
import cn.nekocode.hot.util.PathUtil;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class InstallManager extends BaseInstallManager {
    private static final String COLUMN_EXTENSION = BuildConfig.COLUMN_EXTENSION;


    public InstallManager(BaseFileManager fileManager) {
        super(fileManager);
    }

    @Override
    @Nullable
    public Column install(@NonNull File packageFile) {
        if (!packageFile.exists() ||
                !COLUMN_EXTENSION.equals(PathUtil.getFileExtension(packageFile.getPath()))) {

            return null;
        }

        final File columnDir = getFileManager().getColumnDirectory();

        final Column column = new Column();
        return column;
    }

    @Override
    public boolean uninstall(@NonNull UUID columnId) {
        return true;
    }

    @Override
    public boolean isInstalled(@NonNull UUID columnId) {
        return true;
    }
}
