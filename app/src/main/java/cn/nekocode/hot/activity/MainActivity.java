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

package cn.nekocode.hot.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import cn.nekocode.hot.R;
import cn.nekocode.hot.luaj.HotLuaGlobals;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class MainActivity extends AppCompatActivity {
    private HotLuaGlobals luaGlobals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        luaGlobals = new HotLuaGlobals(this);

        try {
            final LuaValue activity = CoerceJavaToLua.coerce(this);
            luaGlobals.loadfile("test.lua").call(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }

        getWindow().getDecorView().postDelayed(this::f, 2000);
    }

    public void t(String t) {
        Toast.makeText(this, t, Toast.LENGTH_SHORT).show();
    }

    public void f() {
        LuaValue f = luaGlobals.get("test");
        if (!f.isnil()) {
            try {
                f.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
