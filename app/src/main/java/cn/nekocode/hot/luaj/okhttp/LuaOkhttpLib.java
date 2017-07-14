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

package cn.nekocode.hot.luaj.okhttp;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class LuaOkhttpLib extends VarArgFunction {
    public static OkHttpClient sClient;
    private static final String LIB_NAME = "okhttp";
    private static final int INIT = 0;
    private static final int GET = 1;
    private static final int POST = 2;
    private static final int PUT = 3;
    private static final int DELETE = 4;

    private static final String[] NAMES = {
            "get",
            "post",
            "put",
            "delete",
    };

    @Override
    public Varargs invoke(Varargs args) {
        try {
            switch (opcode) {
                case INIT: {
                    final LuaValue env = args.arg(2);
                    final LuaTable t = new LuaTable();
                    bind(t, this.getClass(), NAMES, GET);
                    env.set(LIB_NAME, t);
                    env.get("package").get("loaded").set(LIB_NAME, t);
                    return t;
                }

                case GET: {
                    return get(args);
                }

                case POST: {
                    return post(args);
                }

                case PUT: {
                    return put(args);
                }

                case DELETE: {
                    return delete(args);
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

    private static LuaTable toLuaResponse(Response response) throws IOException {
        final ResponseBody body = response.body();

        final LuaTable luaResponse = new LuaTable();
        luaResponse.set("code", response.code());
        luaResponse.set("message", response.message());
        if (body != null) {
            luaResponse.set("body", body.string());
        } else {
            luaResponse.set("body", NIL);
        }

        return luaResponse;
    }

    /**
     * get(String url)
     */
    private LuaValue get(Varargs args) throws IOException {
        if (sClient != null) {
            final LuaValue arg1 = args.arg1();
            if (arg1.isstring()) {
                final Request request = new Request.Builder()
                        .url(arg1.checkjstring())
                        .build();

                return toLuaResponse(sClient.newCall(request).execute());
            }
        }

        return NIL;
    }

    /**
     * get(String url)
     */
    private LuaValue post(Varargs args) {
        if (sClient != null) {
            final LuaValue arg1 = args.arg1();
            if (arg1.isstring()) {
                final Request request = new Request.Builder()
                        .url(arg1.checkjstring())
                        .post(formBody)
                        .build();

                return toLuaResponse(sClient.newCall(request).execute());
            }
        }

        return NIL;
    }

    private LuaValue put(Varargs args) {
        return NIL;
    }

    private LuaValue delete(Varargs args) {
        return NIL;
    }
}
