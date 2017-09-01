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

package cn.nekocode.hot.luaj;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.BaseLib;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.CoroutineLib;
import org.luaj.vm2.lib.MathLib;
import org.luaj.vm2.lib.OsLib;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.StringLib;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.jse.LuajavaLib;

import java.io.File;
import java.io.FileInputStream;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class HotLuaGlobals extends Globals {

    static {
        LuajavaLib.sClassLoader = new HotClassLoader();
    }

    public HotLuaGlobals(final File baseDir) {
        install();

        this.finder = path -> {
            if (path.contains("../")) {
                // Skip the file if its path is not security
                return null;
            }

            try {
                final File file = new File(baseDir, path);
                if (file.exists()) {
                    return new FileInputStream(file);
                }

            } catch (Throwable t) {
                throw new LuaError(t);
            }
            return null;
        };
    }

    private void install() {
        load(new BaseLib());
        load(new PackageLib());
        load(new Bit32Lib());
        load(new OsLib());
        load(new MathLib());
        load(new TableLib());
        load(new StringLib());
        load(new CoroutineLib());
        load(new LuajavaLib());

        LoadState.install(this);
        LuaC.install(this);
    }

    /**
     * Custom Class Loader
     */
    private static class HotClassLoader extends ClassLoader {
        static final String[] PATH_WHITELIST = new String[] {
                "cn.nekocode.hot.data.model.",
                "okhttp3.",
                "org.jsoup.",
        };
        static final String[] PATH_BLACKLIST = new String[] {
        };

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            // Check black-list
            for (String path : PATH_BLACKLIST) {
                if (name.startsWith(path)) {
                    throw new ClassNotFoundException(name);
                }
            }

            // Check white-list
            for (String path : PATH_WHITELIST) {
                if (name.startsWith(path)) {
                    return Class.forName(name);
                }
            }

            // Use system default class loader
            return ClassLoader.getSystemClassLoader().loadClass(name);
        }
    }
}
