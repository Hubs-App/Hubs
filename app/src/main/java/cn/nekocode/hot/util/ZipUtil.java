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

package cn.nekocode.hot.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class ZipUtil {

    /**
     * Read text of file in zip
     */
    @Nullable
    public static String readFileFromZip(@NonNull File file, @NonNull String path) throws IOException {
        final ZipFile zipFile = new ZipFile(file);
        ByteArrayOutputStream resultOut = null;

        final Enumeration<?> entries = zipFile.entries();
        ZipEntry zipEntry;

        while ((zipEntry = ((ZipEntry) entries.nextElement())) != null) {
            if (zipEntry.isDirectory() || !path.equals(zipEntry.getName())) {
                continue;
            }

            InputStream in = null;
            OutputStream out = null;
            resultOut = new ByteArrayOutputStream(1024);

            try {
                in = new BufferedInputStream(zipFile.getInputStream(zipEntry));
                out = new BufferedOutputStream(resultOut);
                byte buffer[] = new byte[1024];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }

            } finally {
                close(in, out);
            }

            break;
        }

        return resultOut != null ? resultOut.toString() : null;
    }

    /**
     * Unzip all files in zip to dest directory
     */
    @NonNull
    public static List<File> unzipFile(@NonNull File file, @NonNull File destDir) throws IOException {
        final List<File> targetFiles = new ArrayList<>();
        final ZipFile zipFile = new ZipFile(file);

        final Enumeration<?> entries = zipFile.entries();
        ZipEntry zipEntry;

        while ((zipEntry = ((ZipEntry) entries.nextElement())) != null) {
            if (zipEntry.getName().contains("../")) {
                // Skip the file if its path is not security
                continue;
            }

            final File targetFile = new File(destDir.getPath() + File.separator + zipEntry.getName());

            if (zipEntry.isDirectory()) {
                if (targetFile.mkdirs()) {
                    targetFiles.add(targetFile);
                }

            } else {
                InputStream in = null;
                OutputStream out = null;

                try {
                    in = new BufferedInputStream(zipFile.getInputStream(zipEntry));
                    out = new BufferedOutputStream(new FileOutputStream(targetFile));
                    byte buffer[] = new byte[1024];
                    int len;
                    while ((len = in.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                    targetFiles.add(targetFile);

                } finally {
                    close(in, out);
                }
            }
        }

        return targetFiles;
    }

    private static void close(Closeable... closeables) throws IOException {
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                closeable.close();
            }
        }
    }
}
