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

package cn.nekocode.hubs.luaj;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;

import java.io.File;
import java.util.ArrayList;

import cn.nekocode.hubs.HubsApplication;
import cn.nekocode.hubs.data.model.Article;
import cn.nekocode.hubs.data.model.Column;
import cn.nekocode.hubs.luaj.sandbox.HubsLuaGlobals;
import io.reactivex.Single;
import okhttp3.OkHttpClient;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class EntryLuaBridge {
    private Context mContext;
    private Column mColumn;
    @Nullable
    private Globals mLuaGlobals;


    public static EntryLuaBridge create(@NonNull Context context, @NonNull Column column) {
        final EntryLuaBridge bridge = new EntryLuaBridge();
        bridge.mContext = context;
        bridge.mColumn = column;
        return bridge;
    }

    private EntryLuaBridge() {
    }

    @NonNull
    private Globals getGlobals() {
        if (mLuaGlobals == null) {
            final File columnDir =
                    HubsApplication.getDefaultFileManager(mContext).getColumnDirectory(mColumn.getId());
            mLuaGlobals = new HubsLuaGlobals(columnDir);

            mColumn.setAllTo(mLuaGlobals);

            final OkHttpClient client = HubsApplication.getDefaultOkHttpClient(mContext);
            mLuaGlobals.loadfile(mColumn.getEntry()).call(CoerceJavaToLua.coerce(client));
        }

        return mLuaGlobals;
    }

    @NonNull
    public Single<ArrayList<Article>> getArticles(int page) {
        return Single.create(emitter -> {
            final LuaValue func = getGlobals().get("getItems");
            if (func.isnil() || !func.isfunction()) {
                emitter.tryOnError(new Exception("Function getItems not found."));
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
