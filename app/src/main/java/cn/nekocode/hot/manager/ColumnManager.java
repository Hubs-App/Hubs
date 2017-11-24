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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cn.nekocode.hot.BuildConfig;
import cn.nekocode.hot.data.model.Column;
import cn.nekocode.hot.data.model.UserConfig;
import cn.nekocode.hot.manager.base.BaseFileManager;
import cn.nekocode.hot.manager.base.BaseColumnManager;
import cn.nekocode.hot.util.ZipUtil;
import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class ColumnManager extends BaseColumnManager {
    private static final String COLUMN_CONFIG_PATH = BuildConfig.COLUMN_CONFIG_PATH;
    private static final String COLUMN_USER_CONFIG_PATH = BuildConfig.COLUMN_USER_CONFIG_PATH;
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
                emitter.onSuccess(
                        Column.fromLua(ZipUtil.readStringFromZip(packageFile, COLUMN_CONFIG_PATH)));

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
                emitter.onSuccess(
                        Column.fromLua(readConfigToString(columnId)));

            } catch (Exception e) {
                emitter.tryOnError(e);
            }
        });
    }

    private String readFileToString(File file) throws IOException {
        InputStream in = null;
        ByteArrayOutputStream out = null;

        try {
            in = new FileInputStream(file);
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

    private String readConfigToString(UUID columnId) throws IOException {
        final File columnDirectory = getFileManager().getColumnDirectory(columnId);
        // Read default config
        String configStr = readFileToString(new File(columnDirectory, COLUMN_CONFIG_PATH));

        final File userConfig = new File(columnDirectory, COLUMN_USER_CONFIG_PATH);
        if (userConfig.exists()) {
            // Read user config if exists
            configStr += "\n\n" + readFileToString(userConfig);
        }

        return configStr;
    }

    @Override
    public Completable writeUserConfig(@NonNull UUID columnId, @NonNull UserConfig config) {
        return Completable.create(emitter -> {
            PrintWriter writer = null;

            try {
                final File userConfig = new File(
                        getFileManager().getColumnDirectory(columnId), COLUMN_USER_CONFIG_PATH);

                // Recreate new user config file
                for (int i = 0; i < 3 && !userConfig.delete();) { i ++; }
                if (!userConfig.createNewFile()) {
                    emitter.tryOnError(new Exception("Recreate new user config file failed"));
                    return;
                }

                // Write to file
                writer = new PrintWriter(userConfig);
                Object value;
                String line;
                for (Map.Entry<String, Object> entry : config.entrySet()) {
                    value = entry.getValue();
                    line = entry.getKey() + "=";

                    if (value instanceof String) {
                        line += "\"" + value + "\"";

                    } else if (value instanceof Integer || value instanceof Long ||
                            value instanceof Float || value instanceof Double || value instanceof Byte) {
                        line += String.valueOf(value);

                    } else if (value instanceof Boolean) {
                        line += ((Boolean) value) ? "true" : "false";
                    }

                    writer.println(line);
                }
                emitter.onComplete();

            } catch (Exception e) {
                emitter.tryOnError(e);

            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
        });
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
                            column = Column.fromLua(
                                    readConfigToString(UUID.fromString(child.getName())));
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
