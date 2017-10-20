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
import android.support.annotation.NonNull;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;

import java.io.File;
import java.util.ArrayList;

import cn.nekocode.hot.HotApplication;
import cn.nekocode.hot.data.model.Article;
import cn.nekocode.hot.data.model.Column;
import io.reactivex.Single;
import okhttp3.OkHttpClient;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class ColumnLuaBridge {
    private HotLuaGlobals mLuaGlobals;
    private Column mColumn;


    public static ColumnLuaBridge load(@NonNull Context context, @NonNull Column column) {
        final File columnDir =
                HotApplication.getDefaultFileManager(context).getColumnDirectory(column.getId());
        return new ColumnLuaBridge(context, new HotLuaGlobals(columnDir), column);
    }

    private ColumnLuaBridge() {
    }

    private ColumnLuaBridge(Context context, HotLuaGlobals luaGlobals, Column column) {
        mLuaGlobals = luaGlobals;
        mColumn = column;

        try {
            final OkHttpClient client = HotApplication.getDefaultOkHttpClient(context);
            mLuaGlobals.loadfile(mColumn.getEntry()).call(CoerceJavaToLua.coerce(client));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Single<ArrayList<Article>> getArticles(int page) {
        return Single.create(emitter -> {
            final LuaValue func = mLuaGlobals.get("getArticles");
            if (func.isnil()) {
                emitter.tryOnError(new Exception("getArticles return nil"));
            }

            try {
                final LuaValue rlt = func.call(LuaValue.valueOf(page));
                emitter.onSuccess((ArrayList<Article>) CoerceLuaToJava.coerce(rlt, ArrayList.class));

            } catch (Exception e) {
                emitter.tryOnError(e);
            }
        });
    }
}
