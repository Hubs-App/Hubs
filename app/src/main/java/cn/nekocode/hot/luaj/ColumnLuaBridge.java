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
import android.support.annotation.Nullable;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;

import java.util.ArrayList;

import cn.nekocode.hot.data.model.Article;
import cn.nekocode.hot.data.model.Column;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class ColumnLuaBridge {
    private HotLuaGlobals mLuaGlobals;
    private Column mColumn;


    public static ColumnLuaBridge load(@NonNull Context context, @NonNull Column column) {
        return new ColumnLuaBridge(new HotLuaGlobals(context), column);
    }

    private ColumnLuaBridge() {
    }

    private ColumnLuaBridge(HotLuaGlobals luaGlobals, Column column) {
        mLuaGlobals = luaGlobals;
        mColumn = column;

        try {
            mLuaGlobals.loadfile("main.lua").call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public ArrayList<Article> getArticles(int page) {
        final LuaValue func = mLuaGlobals.get("getArticles");
        if (func.isnil()) {
            return null;
        }

        try {
            final LuaValue rlt = func.call(LuaValue.valueOf(page));
            return (ArrayList<Article>) CoerceLuaToJava.coerce(rlt, ArrayList.class);

        } catch (Exception e) {
            return null;
        }
    }
}
