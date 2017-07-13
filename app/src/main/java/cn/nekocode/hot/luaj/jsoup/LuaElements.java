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

package cn.nekocode.hot.luaj.jsoup;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class LuaElements extends LuaTable {
    private final Elements mElements;


    public LuaElements(Elements obj) {
        super();
        mElements = obj;
        int i = 0;
        for (Element element : obj) {
            set(0, new LuaElement(element));
            i ++;
        }
    }

    @Override
    public LuaValue get(LuaValue key) {
        if (key.isint()) {
            return new LuaElement(mElements.get(key.checkint()));
        }

        if (!key.isstring()) {
            return NIL;
        }

        final String funcName = key.checkjstring();
        switch (funcName) {
            case "text":
                return new VarArgFunction() {
                    @Override
                    public Varargs onInvoke(Varargs args) {
                        return LuaElements.valueOf(mElements.text());
                    }
                };

            case "html":
                return new VarArgFunction() {
                    @Override
                    public Varargs onInvoke(Varargs args) {
                        return LuaElements.valueOf(mElements.html());
                    }
                };

            case "outerHtml":
                return new VarArgFunction() {
                    @Override
                    public Varargs onInvoke(Varargs args) {
                        return LuaElements.valueOf(mElements.outerHtml());
                    }
                };

            case "first":
                return new VarArgFunction() {
                    @Override
                    public Varargs onInvoke(Varargs args) {
                        return new LuaElement(mElements.first());
                    }
                };

            case "last":
                return new VarArgFunction() {
                    @Override
                    public Varargs onInvoke(Varargs args) {
                        return new LuaElement(mElements.last());
                    }
                };

            case "size":
                return new VarArgFunction() {
                    @Override
                    public Varargs onInvoke(Varargs args) {
                        return new LuaElement(mElements.last());
                    }
                };

            default:
                throw new LuaError("not yet supported: " + funcName);
        }
    }
}