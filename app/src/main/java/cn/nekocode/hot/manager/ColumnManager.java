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

import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.compiler.LuaC;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cn.nekocode.hot.BuildConfig;
import cn.nekocode.hot.data.model.Column;
import cn.nekocode.hot.manager.base.BaseFileManager;
import cn.nekocode.hot.manager.base.BaseColumnManager;
import cn.nekocode.hot.util.ZipUtil;
import io.reactivex.Single;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class ColumnManager extends BaseColumnManager {
    private static final String COLUMN_CONFIG_PATH = BuildConfig.COLUMN_CONFIG_PATH;
    private final Globals mGlobals;


    public ColumnManager(BaseFileManager fileManager) {
        super(fileManager);

        // Obatain a lua globals for loading configs
        mGlobals = new Globals();
        LoadState.install(mGlobals);
        LuaC.install(mGlobals);
    }

    @Override
    @NonNull
    public Single<Column> readConfig(@NonNull File packageFile) {
        return Single.create(emitter -> {
            try {
                final Column column = Column.fromLua(
                        ZipUtil.readStringFromZip(packageFile, COLUMN_CONFIG_PATH), mGlobals);

                if (checkIsTypeSupported(column.getType())) {
                    emitter.onSuccess(column);
                } else {
                    emitter.tryOnError(new Exception("Not supported column type."));
                }

            } catch (Exception e) {
                emitter.tryOnError(e);
            }
        });
    }

    @Override
    @NonNull
    public Single<Column> readConfig(@NonNull UUID columnId) {
        return Single.create(emitter -> {
            try {
                final Column column = Column.fromLua(readConfigToString(columnId), mGlobals);

                if (checkIsTypeSupported(column.getType())) {
                    emitter.onSuccess(column);
                } else {
                    emitter.tryOnError(new Exception("Not supported column type."));
                }

            } catch (Exception e) {
                emitter.tryOnError(e);
            }
        });
    }

    private String readConfigToString(UUID columnId) throws IOException {
        InputStream in = null;
        ByteArrayOutputStream out = null;

        try {
            final File configFile = new File(getFileManager().getColumnDirectory(columnId), COLUMN_CONFIG_PATH);

            in = new FileInputStream(configFile);
            out = new ByteArrayOutputStream(1024);

            byte buffer[] = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }

            return out.toString();

        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (Exception ignored) {

            }
        }
    }

    private boolean checkIsTypeSupported(String columnType) {
        for (String supportedType : Column.SUPPORTED_TYPES) {
            if (supportedType.equals(columnType)) {
                return true;
            }
        }

        return false;
    }

    @Override
    @NonNull
    public Single<Column> install(@NonNull Context context, @NonNull File packageFile) {
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
    public Single<Boolean> uninstall(@NonNull UUID columnId) {
        return Single.create(emitter -> {
            final File columnDir = getFileManager().getColumnDirectory(columnId);

            if (!columnDir.exists()) {
                emitter.onSuccess(true);

            } else {
                boolean rlt[] = new boolean[]{true};
                deleteRecursive(columnDir, rlt);
                emitter.onSuccess(rlt[0]);
            }
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
        final File columnDir = getFileManager().getColumnDirectory(columnId);
        return columnDir.exists() && new File(columnDir, BuildConfig.COLUMN_CONFIG_PATH).exists();
    }

    @Override
    @NonNull
    public Single<List<Column>> getAllInstalled() {
        return Single.create(emitter -> {
            final ArrayList<Column> columns = new ArrayList<>();
            final File columnsDir = getFileManager().getColumnsDirectory();

            if (!columnsDir.exists() || !columnsDir.isDirectory()) {
                emitter.onSuccess(columns);
                return;
            }

            try {
                Column column;
                for (File child : columnsDir.listFiles()) {
                    if (child.isDirectory()) {
                        try {
                            column = Column.fromLua(readConfigToString(UUID.fromString(child.getName())), mGlobals);
                            columns.add(column);
                        } catch (Exception ignored) {
                            // Just skip this column if load failed
                        }
                    }
                }

                emitter.onSuccess(columns);

            } catch (Exception e) {
                emitter.tryOnError(e);
            }
        });
    }
}
