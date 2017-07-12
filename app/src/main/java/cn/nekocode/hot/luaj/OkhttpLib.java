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

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class OkhttpLib extends VarArgFunction {
    private static final String LIB_NAME = "okhttp";
    private static final int INIT = 0;
    private static final int TEST = 1;

    private static final String[] NAMES = {
            "test"
    };

    @Override
    public Varargs invoke(Varargs args) {
        try {
            switch (opcode) {
                case INIT: {
                    LuaValue env = args.arg(2);
                    LuaTable t = new LuaTable();
                    bind(t, this.getClass(), NAMES, TEST);
                    env.set(LIB_NAME, t);
                    env.get("package").get("loaded").set(LIB_NAME, t);
                    return t;
                }

                case TEST: {
                    return NIL;
                }

                default:
                    throw new LuaError("not yet supported: " + this);
            }

        } catch (LuaError e) {
            throw e;

        } catch (Exception e) {
            throw new LuaError(e);
        }
    }
}
