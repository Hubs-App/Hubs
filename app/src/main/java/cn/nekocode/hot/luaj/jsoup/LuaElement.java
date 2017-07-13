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
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class LuaElement extends LuaUserdata {
    private final Element mElement;


    public LuaElement(Element obj) {
        super(obj);
        mElement = obj;
    }

    @Override
    public LuaValue get(LuaValue key) {
        if (!key.isstring()) {
            return NIL;
        }

        final String funcName = key.checkjstring();
        switch (funcName) {
            case "tagName":
                return new VarArgFunction() {
                    @Override
                    public Varargs onInvoke(Varargs args) {
                        return LuaElement.valueOf(mElement.tagName());
                    }
                };

            case "id":
                return new VarArgFunction() {
                    @Override
                    public Varargs onInvoke(Varargs args) {
                        return LuaElement.valueOf(mElement.id());
                    }
                };

            case "attr":
                return new VarArgFunction() {
                    @Override
                    public Varargs onInvoke(Varargs args) {
                        final LuaValue arg1 = args.arg1();
                        if (arg1.isstring()) {
                            return LuaElement.valueOf(mElement.attr(arg1.checkjstring()));
                        }
                        return NIL;
                    }
                };

            case "parent":
                return new VarArgFunction() {
                    @Override
                    public Varargs onInvoke(Varargs args) {
                        return new LuaElement(mElement.parent());
                    }
                };

            case "child":
                return new VarArgFunction() {
                    @Override
                    public Varargs onInvoke(Varargs args) {
                        final LuaValue arg1 = args.arg1();
                        if (arg1.isint()) {
                            return new LuaElement(mElement.child(arg1.checkint()));
                        }
                        return NIL;
                    }
                };

            case "children":
                return new VarArgFunction() {
                    @Override
                    public Varargs onInvoke(Varargs args) {
                        return new LuaElements(mElement.children());
                    }
                };

            case "select":
                return new VarArgFunction() {
                    @Override
                    public Varargs onInvoke(Varargs args) {
                        final LuaValue arg1 = args.arg1();
                        if (arg1.isstring()) {
                            return new LuaElements(mElement.select(arg1.checkjstring()));
                        }
                        return NIL;
                    }
                };

            case "text":
                return new VarArgFunction() {
                    @Override
                    public Varargs onInvoke(Varargs args) {
                        return LuaElement.valueOf(mElement.text());
                    }
                };

            case "ownText":
                return new VarArgFunction() {
                    @Override
                    public Varargs onInvoke(Varargs args) {
                        return LuaElement.valueOf(mElement.ownText());
                    }
                };

            case "html":
                return new VarArgFunction() {
                    @Override
                    public Varargs onInvoke(Varargs args) {
                        return LuaElement.valueOf(mElement.html());
                    }
                };

            case "outerHtml":
                return new VarArgFunction() {
                    @Override
                    public Varargs onInvoke(Varargs args) {
                        return LuaElement.valueOf(mElement.outerHtml());
                    }
                };

            default:
                throw new LuaError("not yet supported: " + funcName);
        }
    }
}
