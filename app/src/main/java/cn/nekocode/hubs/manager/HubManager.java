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

package cn.nekocode.hubs.manager;

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

import cn.nekocode.hubs.BuildConfig;
import cn.nekocode.hubs.data.model.Hub;
import cn.nekocode.hubs.data.model.UserConfig;
import cn.nekocode.hubs.manager.base.BaseFileManager;
import cn.nekocode.hubs.manager.base.BaseHubManager;
import cn.nekocode.hubs.util.ZipUtil;
import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class HubManager extends BaseHubManager {
    private static final String HUB_CONFIG_PATH = BuildConfig.HUB_CONFIG_PATH;
    private static final String HUB_USER_CONFIG_PATH = BuildConfig.HUB_USER_CONFIG_PATH;
    private final Globals mGlobals;


    public HubManager(BaseFileManager fileManager) {
        super(fileManager);

        // Obatain a lua globals for loading configs
        mGlobals = new Globals();
        LoadState.install(mGlobals);
        LuaC.install(mGlobals);
    }

    @Override
    @NonNull
    public Single<Hub> readConfig(@NonNull File packageFile) {
        return Single.create(emitter -> {
            try {
                emitter.onSuccess(
                        Hub.fromLua(ZipUtil.readStringFromZip(packageFile, HUB_CONFIG_PATH)));

            } catch (Exception e) {
                emitter.tryOnError(e);
            }
        });
    }

    @Override
    @NonNull
    public Single<Hub> readConfig(@NonNull String hubId) {
        return Single.create(emitter -> {
            try {
                emitter.onSuccess(
                        Hub.fromLua(readConfigToString(hubId)));

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

    private String readConfigToString(String hubId) throws IOException {
        final File hubDirectory = getFileManager().getHubDirectory(hubId);
        // Read default config
        String configStr = readFileToString(new File(hubDirectory, HUB_CONFIG_PATH));

        final File userConfig = new File(hubDirectory, HUB_USER_CONFIG_PATH);
        if (userConfig.exists()) {
            // Read user config if exists
            configStr += "\n\n" + readFileToString(userConfig);
        }

        return configStr;
    }

    @Override
    public Completable writeUserConfig(@NonNull String hubId, @NonNull UserConfig config) {
        return Completable.create(emitter -> {
            PrintWriter writer = null;

            try {
                final File userConfig = new File(
                        getFileManager().getHubDirectory(hubId), HUB_USER_CONFIG_PATH);

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
    public Single<Hub> install(@NonNull Context context, @NonNull File packageFile) {
        return readConfig(packageFile)
                // Firstly, remove old directory
                .flatMap(hub ->
                        uninstall(hub.getId())
                                .map(success -> {
                                    if (!success) {
                                        throw new Exception("Remove old hub directory failed.");
                                    }
                                    return hub;
                                }))
                // Unzip package
                .map(hub -> {
                    final File hubDir = getFileManager().getHubDirectory(hub.getId());

                    if (!hubDir.mkdirs()) {
                        throw new Exception("Create hub directory failed.");
                    }

                    ZipUtil.unzipFile(packageFile, hubDir);
                    return hub;
                });
    }

    @Override
    @NonNull
    public Single<Boolean> uninstall(@NonNull String hubId) {
        return Single.create(emitter -> {
            final File hubDir = getFileManager().getHubDirectory(hubId);

            if (!hubDir.exists()) {
                emitter.onSuccess(true);

            } else {
                boolean rlt[] = new boolean[]{true};
                deleteRecursive(hubDir, rlt);
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
    public boolean isInstalled(@NonNull String hubId) {
        final File hubDir = getFileManager().getHubDirectory(hubId);
        return hubDir.exists() && new File(hubDir, BuildConfig.HUB_CONFIG_PATH).exists();
    }

    @Override
    @NonNull
    public Single<List<Hub>> getAllInstalled() {
        return Single.create(emitter -> {
            final ArrayList<Hub> hubs = new ArrayList<>();
            final File hubsDir = getFileManager().getHubsDirectory();

            if (!hubsDir.exists() || !hubsDir.isDirectory()) {
                emitter.onSuccess(hubs);
                return;
            }

            try {
                Hub hub;
                for (File child : hubsDir.listFiles()) {
                    if (child.isDirectory()) {
                        try {
                            hub = Hub.fromLua(
                                    readConfigToString(child.getName()));
                            hubs.add(hub);
                        } catch (Exception ignored) {
                            // Just skip this hub if load failed
                        }
                    }
                }

                emitter.onSuccess(hubs);

            } catch (Exception e) {
                emitter.tryOnError(e);
            }
        });
    }
}
