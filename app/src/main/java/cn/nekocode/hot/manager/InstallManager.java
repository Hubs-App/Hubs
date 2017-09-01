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

import java.io.File;
import java.util.UUID;

import cn.nekocode.hot.BuildConfig;
import cn.nekocode.hot.data.model.Column;
import cn.nekocode.hot.manager.base.BaseFileManager;
import cn.nekocode.hot.manager.base.BaseInstallManager;
import cn.nekocode.hot.util.ZipUtil;
import io.reactivex.Observable;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class InstallManager extends BaseInstallManager {
    private static final String COLUMN_CONFIG_PATH = BuildConfig.COLUMN_CONFIG_PATH;


    public InstallManager(BaseFileManager fileManager) {
        super(fileManager);
    }

    @Override
    @NonNull
    public Observable<Column> readConfig(@NonNull File packageFile) {
        return Observable.create(emitter -> {
            try {
                final Column column = Column.fromLua(
                        ZipUtil.readStringFromZip(packageFile, COLUMN_CONFIG_PATH));
                emitter.onNext(column);
                emitter.onComplete();

            } catch (Exception e) {
                emitter.tryOnError(e);
            }
        });
    }

    @Override
    @NonNull
    public Observable<Column> install(@NonNull Context context, @NonNull File packageFile) {
        return readConfig(packageFile)
                // Firstly, remove old directory
                .flatMap(column ->
                        uninstall(column.getId())
                                .map(success -> {
                                    if (!success) {
                                        throw new Exception("Remove old column directory failed.");
                                    }
                                    return column;
                                }))
                // Unzip package
                .map(column -> {
                    final File columnDir = getFileManager().getColumnDirectory(column.getId());

                    if (!columnDir.mkdirs()) {
                        throw new Exception("Create column directory failed.");
                    }

                    ZipUtil.unzipFile(packageFile, columnDir);
                    return column;
                });
    }

    @Override
    @NonNull
    public Observable<Boolean> uninstall(@NonNull UUID columnId) {
        return Observable.create(emitter -> {
            final File columnDir = getFileManager().getColumnDirectory(columnId);

            if (!columnDir.exists()) {
                emitter.onNext(true);

            } else {
                boolean rlt[] = new boolean[]{true};
                deleteRecursive(columnDir, rlt);
                emitter.onNext(rlt[0]);
            }
            emitter.onComplete();
        });
    }

    /**
     * Delete a whole folder and content
     */
    private void deleteRecursive(File fileOrDirectory, boolean[] rlt) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child, rlt);

        rlt[0] &= fileOrDirectory.delete();
    }

    @Override
    public boolean isInstalled(@NonNull UUID columnId) {
        return getFileManager().getColumnDirectory(columnId).exists();
    }
}
