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
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaDouble;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;

import java.util.HashMap;
import java.util.Map;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class ColumnExtra extends HashMap<String, Object> implements Parcelable {

    /**
     * Put LuaValue to map
     */
    public void put(String key, LuaValue value) {
        if (value instanceof LuaDouble) {
            put(key, value.todouble());
        } else if (value instanceof LuaInteger) {
            put(key, value.toint());
        } else if (value instanceof LuaString) {
            put(key, value.tojstring());
        } else if (value instanceof LuaBoolean) {
            put(key, value.toboolean());
        }
    }

    /**
     * Set all key-value pairs to lua globals
     */
    public void setAllTo(Globals globals) {
        for (Map.Entry<String, Object> entry : entrySet()) {
            final Object value = entry.getValue();

            if (value instanceof Integer) {
                globals.set(entry.getKey(), (Integer) entry.getValue());
            } else if (value instanceof Long) {
                globals.set(entry.getKey(), (Long) entry.getValue());
            } else if (value instanceof Float) {
                globals.set(entry.getKey(), (Float) entry.getValue());
            } else if (value instanceof Double) {
                globals.set(entry.getKey(), (Double) entry.getValue());
            } else if (value instanceof String) {
                globals.set(entry.getKey(), (String) entry.getValue());
            } else if (value instanceof Byte) {
                globals.set(entry.getKey(), (Byte) entry.getValue());
            } else if (value instanceof Boolean) {
                globals.set(entry.getKey(), (Boolean) entry.getValue() ? LuaValue.TRUE : LuaValue.FALSE);
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(size());

        for (Map.Entry<String, Object> entry : entrySet()) {
            final Object value = entry.getValue();

            if (value instanceof Integer) {
                dest.writeByte((byte) 1);
                dest.writeString(entry.getKey());
                dest.writeInt((Integer) value);
            } else if (value instanceof Long) {
                dest.writeByte((byte) 2);
                dest.writeString(entry.getKey());
                dest.writeLong((Long) value);
            } else if (value instanceof Float) {
                dest.writeByte((byte) 3);
                dest.writeString(entry.getKey());
                dest.writeFloat((Float) value);
            } else if (value instanceof Double) {
                dest.writeByte((byte) 4);
                dest.writeString(entry.getKey());
                dest.writeDouble((Double) value);
            } else if (value instanceof String) {
                dest.writeByte((byte) 5);
                dest.writeString(entry.getKey());
                dest.writeString((String) value);
            } else if (value instanceof Byte) {
                dest.writeByte((byte) 6);
                dest.writeString(entry.getKey());
                dest.writeByte((Byte) value);
            } else if (value instanceof Boolean) {
                dest.writeByte((byte) 7);
                dest.writeString(entry.getKey());
                dest.writeByte(((Boolean) value) ? (byte) 1 : 0);
            } else {
                throw new RuntimeException("Unsupported type.");
            }
        }
    }

    public ColumnExtra() {
    }

    protected ColumnExtra(Parcel in) {
        final int size = in.readInt();

        for (int i = 0; i < size; i++) {
            final byte type = in.readByte();
            final String key = in.readString();
            switch (type) {
                case 1:
                    put(key, in.readInt());
                    break;
                case 2:
                    put(key, in.readLong());
                    break;
                case 3:
                    put(key, in.readFloat());
                    break;
                case 4:
                    put(key, in.readDouble());
                    break;
                case 5:
                    put(key, in.readString());
                    break;
                case 6:
                    put(key, in.readByte());
                    break;
                case 7:
                    put(key, in.readByte() == 1);
                    break;
            }
        }
    }

    public static final Creator<ColumnExtra> CREATOR = new Creator<ColumnExtra>() {
        @Override
        public ColumnExtra createFromParcel(Parcel source) {
            return new ColumnExtra(source);
        }

        @Override
        public ColumnExtra[] newArray(int size) {
            return new ColumnExtra[size];
        }
    };
}
