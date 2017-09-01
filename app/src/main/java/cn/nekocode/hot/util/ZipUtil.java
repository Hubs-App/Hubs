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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class ZipUtil {

    /**
     * Read string of entry in the zip file
     */
    @Nullable
    public static String readStringFromZip(@NonNull File file, @NonNull String path) throws IOException {
        return readStringFromZip(new FileInputStream(file), path);
    }

    /**
     * Read string of entry in the zip input stream
     */
    @Nullable
    public static String readStringFromZip(@NonNull InputStream inputStream, @NonNull String path) throws IOException {
        final String rlt[] = new String[] {null};
        unzipFile(inputStream, null, null, path, rlt);
        return rlt[0];
    }

    /**
     * Unzip all files from a zip file
     */
    @NonNull
    public static List<File> unzipFile(@NonNull File file, @NonNull File destDir) throws IOException {
        return unzipFile(new FileInputStream(file), destDir);
    }

    /**
     * Unzip all files from a zip input stream
     */
    @NonNull
    public static List<File> unzipFile(@NonNull InputStream inputStream, @NonNull File destDir) throws IOException {
        final ArrayList<File> rlt = new ArrayList<>();
        unzipFile(inputStream, destDir, rlt, null, null);
        return rlt;
    }

    public static void unzipFile(
            @NonNull InputStream inputStream,
            @Nullable File destDir, @Nullable List<File> targetFiles,
            @Nullable String seekingPath, @Nullable String[] matchedEntryText) throws IOException {

        if (destDir == null && seekingPath == null) {
            return;
        }

        final ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        int isSeeking = (seekingPath == null ? -1 : 0);

        ZipEntry zipEntry;
        String entryName;
        File targetFile;
        ByteArrayOutputStream byteArrayOut;

        final InputStream in = new BufferedInputStream(zipInputStream);
        OutputStream out = null;
        OutputStream out2 = null;

        try {
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                entryName = zipEntry.getName();
                if (entryName.contains("../")) {
                    // Skip the file if its path is not security
                    continue;
                }

                targetFile = (destDir != null ? new File(destDir, zipEntry.getName()) : null);
                byteArrayOut = (seekingPath != null ? new ByteArrayOutputStream(1024) : null);

                if (zipEntry.isDirectory()) {
                    if (targetFile != null) {
                        if (targetFile.mkdirs() && targetFiles != null) {
                            targetFiles.add(targetFile);
                        }
                    }

                    continue;
                }

                if (isSeeking == 0) {
                    if (seekingPath.equals(entryName)) {
                        // Found the matched entry
                        isSeeking = 1;

                    } else if (targetFile == null) {
                        continue;
                    }
                }


                out = targetFile != null ?
                        new BufferedOutputStream(new FileOutputStream(targetFile)) : null;
                out2 = byteArrayOut != null ?
                        new BufferedOutputStream(byteArrayOut) : null;

                byte buffer[] = new byte[1024];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    if (out != null) {
                        out.write(buffer, 0, len);
                    }
                    if (out2 != null) {
                        out2.write(buffer, 0, len);
                    }
                }

                if (targetFile != null && targetFiles != null) {
                    targetFiles.add(targetFile);
                }
                if (isSeeking == 1 && matchedEntryText != null) {
                    out2.flush();
                    matchedEntryText[0] = byteArrayOut.toString();
                    isSeeking = -1;

                    if (destDir == null) {
                        break;
                    }
                }

                close(out, out2);
            }

        } finally {
            close(in, out, out2);
        }
    }

    private static void close(Closeable... closeables) throws IOException {
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                closeable.close();
            }
        }
    }
}
