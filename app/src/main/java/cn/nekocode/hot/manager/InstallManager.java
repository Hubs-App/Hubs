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

import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.compiler.LuaC;

import java.io.File;
import java.util.UUID;

import cn.nekocode.hot.BuildConfig;
import cn.nekocode.hot.data.model.Column;
import cn.nekocode.hot.manager.base.BaseFileManager;
import cn.nekocode.hot.manager.base.BaseInstallManager;
import cn.nekocode.hot.util.PathUtil;
import cn.nekocode.hot.util.ZipUtil;
import io.reactivex.Observable;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class InstallManager extends BaseInstallManager {
    private static final String COLUMN_EXTENSION = BuildConfig.COLUMN_EXTENSION;
    private static final String COLUMN_CONFIG_PATH = "config.lua";


    public InstallManager(BaseFileManager fileManager) {
        super(fileManager);
    }

    @Override
    public Observable<Column> readConfig(@NonNull File packageFile) {
        return Observable.create(emitter -> {
            if (!packageFile.exists() ||
                    !COLUMN_EXTENSION.equals(PathUtil.getFileExtension(packageFile.getPath()))) {

                emitter.tryOnError(new IllegalStateException(
                        "File doesn't exist or is not an available column package."));

            } else {
                try {
                    final Column column = readConfig(ZipUtil.readFileFromZip(packageFile, COLUMN_CONFIG_PATH));
                    emitter.onNext(column);
                    emitter.onComplete();

                } catch (Exception e) {
                    emitter.tryOnError(e);
                }
            }
        });
    }

    /**
     * Read column config from lua script
     */
    private Column readConfig(String text) {
        final Column column = new Column();
        final Globals globals = new Globals();
        LoadState.install(globals);
        LuaC.install(globals);

        globals.load(text).call();
        column.setId(UUID.fromString(globals.get("uuid").checkjstring()));
        column.setName(globals.get("name").checkjstring());
        column.setType(globals.get("type").checkjstring());
        column.setVersion(globals.get("version").checkjstring());
        column.setEntry(globals.get("entry").checkjstring());

        return column;
    }

    @Override
    public Observable<Column> install(@NonNull File packageFile) {
        return readConfig(packageFile);
    }

    @Override
    public Observable<Boolean> uninstall(@NonNull UUID columnId) {
        return Observable.create(emitter -> {
            final String columnPath = getColumnPath(columnId);
            final boolean isInstelled = isColumnFilesExist(columnPath);

            if (!isInstelled) {
                emitter.onNext(true);

            } else {
                boolean rlt[] = new boolean[] {true};
                deleteRecursive(new File(columnPath), rlt);
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
        return isColumnFilesExist(getColumnPath(columnId));
    }

    private String getColumnPath(@NonNull UUID columnId) {
        return getFileManager().getColumnDirectory() + File.separator + columnId.toString();
    }

    private boolean isColumnFilesExist(String columnPath) {
        return new File(columnPath).exists() &&
                new File(columnPath + File.separator + COLUMN_CONFIG_PATH).exists();
    }
}
