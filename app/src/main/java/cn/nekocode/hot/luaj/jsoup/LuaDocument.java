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

import org.jsoup.nodes.Document;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class LuaDocument extends LuaElement {
    private final Document mDocument;


    public LuaDocument(Document obj) {
        super(obj);
        mDocument = obj;
    }

    @Override
    public LuaValue get(LuaValue key) {
        if (!key.isstring()) {
            return NIL;
        }

        final String funcName = key.checkjstring();
        switch (funcName) {
            case "head":
                return new VarArgFunction() {
                    @Override
                    public Varargs onInvoke(Varargs args) {
                        return new LuaElement(mDocument.head());
                    }
                };

            case "body":
                return new VarArgFunction() {
                    @Override
                    public Varargs onInvoke(Varargs args) {
                        return new LuaElement(mDocument.body());
                    }
                };

            case "title":
                return new VarArgFunction() {
                    @Override
                    public Varargs onInvoke(Varargs args) {
                        return LuaDocument.valueOf(mDocument.title());
                    }
                };

            default:
                return super.get(key);
        }
    }
}
