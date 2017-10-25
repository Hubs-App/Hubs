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

package cn.nekocode.hot.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaDouble;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.compiler.LuaC;

import java.util.HashMap;
import java.util.UUID;

import cn.nekocode.hot.util.CommonUtil;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class Column implements Parcelable {
    public static final String TYPE_ARTICLE = "article";
    public static final String[] SUPPORTED_TYPES = new String[]{TYPE_ARTICLE};

    private UUID id;
    private String name;
    private String type;
    private String version;
    private String entry;
    private boolean debug = false;
    private HashMap<String, Object> extra = new HashMap<>();


    public static Column fromLua(String luaText, Globals globals) throws Exception {
        final Column column = new Column();
        globals.load(luaText).call();

        final LuaTable table = globals.checktable();
        Varargs pair;
        LuaValue key = LuaValue.NIL;
        LuaValue value;
        String keyStr;
        for (int i = 0; true; i ++) {
            pair = table.next(key);
            if ((key = pair.arg1()).isnil())
                break;

            value = pair.arg(2);
            keyStr = key.checkjstring();
            switch (keyStr) {
                case "uuid":
                    column.setId(UUID.fromString(value.checkjstring()));
                    break;

                case "name":
                    column.setName(value.checkjstring());
                    break;

                case "type":
                    final String type = value.checkjstring();
                    if (checkIsTypeSupported(type)) {
                        column.setType(type);
                    } else {
                        throw new Exception("Not supported column type.");
                    }
                    break;

                case "version":
                    column.setVersion(value.checkjstring());
                    break;

                case "entry":
                    column.setEntry(value.checkjstring());
                    break;

                case "debug":
                    column.setDebug(value.checkboolean());
                    break;

                default:
                    if (value instanceof LuaDouble) {
                        column.extra.put(keyStr, value.todouble());
                    } else if (value instanceof LuaInteger) {
                        column.extra.put(keyStr, value.toint());
                    } else if (value instanceof LuaString) {
                        column.extra.put(keyStr, value.tojstring());
                    } else if (value instanceof LuaBoolean) {
                        column.extra.put(keyStr, value.toboolean());
                    }
                    break;
            }
        }

        return column;
    }

    private static boolean checkIsTypeSupported(String columnType) {
        for (String supportedType : SUPPORTED_TYPES) {
            if (supportedType.equals(columnType)) {
                return true;
            }
        }
        return false;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public HashMap<String, Object> getExtra() {
        return extra;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.id);
        dest.writeString(this.name);
        dest.writeString(this.type);
        dest.writeString(this.version);
        dest.writeString(this.entry);
        dest.writeByte(this.debug ? (byte) 1 : 0);
        CommonUtil.writeToParcel(this.extra, dest, flags);
    }

    public Column() {
    }

    protected Column(Parcel in) {
        this.id = (UUID) in.readSerializable();
        this.name = in.readString();
        this.type = in.readString();
        this.version = in.readString();
        this.entry = in.readString();
        this.debug = (in.readByte() == 1);
        this.extra.clear();
        this.extra.putAll(CommonUtil.createFromParcel(in));
    }

    public static final Creator<Column> CREATOR = new Creator<Column>() {
        @Override
        public Column createFromParcel(Parcel source) {
            return new Column(source);
        }

        @Override
        public Column[] newArray(int size) {
            return new Column[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Column) {
            return id.equals(((Column) obj).getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
