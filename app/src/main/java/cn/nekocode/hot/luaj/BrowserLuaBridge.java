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

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.io.File;

import cn.nekocode.hot.HotApplication;
import cn.nekocode.hot.data.model.Column;
import cn.nekocode.hot.luaj.sandbox.HotLuaGlobals;
import io.reactivex.Single;
import okhttp3.OkHttpClient;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class BrowserLuaBridge {
    private Context mContext;
    private Column mColumn;
    @Nullable
    private Globals mLuaGlobals;


    public static BrowserLuaBridge create(@NonNull Context context, @NonNull Column column) {
        if (column.getBrowser() == null) return null;

        final BrowserLuaBridge bridge = new BrowserLuaBridge();
        bridge.mContext = context;
        bridge.mColumn = column;
        return bridge;
    }

    private BrowserLuaBridge() {
    }

    @NonNull
    private Globals getGlobals() {
        if (mLuaGlobals == null) {
            final File columnDir =
                    HotApplication.getDefaultFileManager(mContext).getColumnDirectory(mColumn.getId());
            mLuaGlobals = new HotLuaGlobals(columnDir);

            mColumn.setAllTo(mLuaGlobals);

            final OkHttpClient client = HotApplication.getDefaultOkHttpClient(mContext);
            mLuaGlobals.loadfile(mColumn.getBrowser()).call(CoerceJavaToLua.coerce(client));
        }

        return mLuaGlobals;
    }

    @NonNull
    public Single<String> onLoadUrl(String url) {
        return Single.create(emitter -> {
            final LuaValue func = getGlobals().get("onLoadUrl");
            if (func.isnil() || !func.isfunction()) {
                emitter.tryOnError(new Exception("Function onLoadUrl not found."));
            }

            try {
                final LuaValue rlt = func.call(LuaValue.valueOf(url));
                emitter.onSuccess(rlt.checkjstring());

            } catch (Exception e) {
                emitter.tryOnError(e);
            }
        });
    }
}
