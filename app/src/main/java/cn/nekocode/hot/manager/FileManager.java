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
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import cn.nekocode.hot.HotApplication;
import cn.nekocode.hot.manager.base.BaseFileManager;
import cn.nekocode.hot.util.PathUtil;
import io.reactivex.Single;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class FileManager extends BaseFileManager {
    private static final String ROOT_DIRECTORY = "HotApp";
    private static final String COLUMNS_DIRECTORY = "Column";
    private static final String SCHEME_FILE = "file";
    private static final String SCHEME_CONTENT = "content";
    private static final String SCHEME_HTTP = "http";
    private static final String SCHEME_HTTPS = "https";


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

        dir = new File(dir, COLUMNS_DIRECTORY);
        if ((!dir.exists()) && (!dir.mkdir())) {
            return false;
        }

        return true;
    }

    @Override
    @NonNull
    public File getRootDirectory() {
        final File dir = PathUtil.getExternalStorageDirectory();
        return new File((dir != null ? dir.getPath() : ""), ROOT_DIRECTORY);
    }

    @Override
    @NonNull
    public File getColumnsDirectory() {
        return new File(getRootDirectory().getPath(), COLUMNS_DIRECTORY);
    }

    @Override
    @NonNull
    public File getColumnDirectory(@NonNull UUID columnId) {
        return new File(getColumnsDirectory().getPath(), columnId.toString());
    }

    @Override
    @NonNull
    public Single<File> getFile(@NonNull Context context, @NonNull Uri uri) {
        final String scheme = uri.getScheme();
        final boolean isFile = SCHEME_FILE.equalsIgnoreCase(scheme);
        final boolean isContent = SCHEME_CONTENT.equalsIgnoreCase(scheme);
        final boolean isHttp = SCHEME_HTTP.equalsIgnoreCase(scheme);
        final boolean isHttps = SCHEME_HTTPS.equalsIgnoreCase(scheme);

        if (isFile || isContent) {
            /*
              Local file
             */
            return Single.create(emitter -> {
                try {
                    final String path = isFile ? uri.getPath() : PathUtil.getRealPathFromURI(context, uri);
                    if (!TextUtils.isEmpty(path)) {
                        emitter.onSuccess(new File(path));

                    } else {
                        emitter.tryOnError(new Exception("Not supported uri."));
                    }

                } catch (Exception e) {
                    emitter.tryOnError(e);
                }
            });

        } else if (isHttp || isHttps) {
            /*
              Remote file
             */
            return Single.create(emitter -> {
                InputStream in = null;
                OutputStream out = null;

                try {
                    final OkHttpClient httpClient = HotApplication.getDefaultOkHttpClient(context);
                    final Request request = new Request.Builder().url(uri.toString()).build();
                    final Response response = httpClient.newCall(request).execute();

                    if (response.isSuccessful()) {
                        // Write to cache file
                        final File cacheFile =
                                new File(context.getCacheDir(), PathUtil.getFileNameFromUrl(uri.toString()));

                        in = new BufferedInputStream(response.body().byteStream());
                        out = new BufferedOutputStream(new FileOutputStream(cacheFile));

                        byte buffer[] = new byte[1024];
                        int len;
                        while ((len = in.read(buffer)) != -1) {
                            out.write(buffer, 0, len);
                        }

                        emitter.onSuccess(cacheFile);

                    } else {
                        emitter.tryOnError(new Exception("Request is not success."));
                    }

                } catch (Exception e) {
                    emitter.tryOnError(e);

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
            });
        }

        return Single.error(new Exception("Not supported uri scheme."));
    }
}
