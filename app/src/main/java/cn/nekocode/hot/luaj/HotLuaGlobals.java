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

import android.content.Context;

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
import org.luaj.vm2.lib.ResourceFinder;
import org.luaj.vm2.lib.StringLib;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.jse.LuajavaLib;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class HotLuaGlobals extends Globals {

    public HotLuaGlobals(final String basePath) {
        install();

        this.finder = new ResourceFinder() {
            @Override
            public InputStream findResource(String path) {
                try {
                    File file = new File(basePath, path);
                    if (!file.exists() && !path.endsWith(".lua")) {
                        file = new File(basePath, path + ".lua");
                    }

                    if (file.exists()) {
                        return new FileInputStream(file);
                    }
                } catch (Throwable t) {
                    throw new LuaError(t);
                }
                return null;
            }
        };
    }

    public HotLuaGlobals(Context context) {
        install();

        final Context app = context.getApplicationContext();
        this.finder = new ResourceFinder() {
            public InputStream findResource(String path) {
                try {
                    if (!path.endsWith(".lua")) {
                        return app.getAssets().open(path + ".lua");
                    } else {
                        return app.getAssets().open(path);
                    }
                } catch (Throwable t) {
                    throw new LuaError(t);
                }
            }
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
//        load(new HotLib());

        LoadState.install(this);
        LuaC.install(this);
    }
}
