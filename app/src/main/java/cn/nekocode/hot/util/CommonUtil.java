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

package cn.nekocode.hot.util;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class CommonUtil {

    @SafeVarargs
    public static <T> ArrayList<T> toArrayList(T... elements) {
        final ArrayList<T> list = new ArrayList<>();
        list.addAll(Arrays.asList(elements));
        return list;
    }

    public static void writeToParcel(Map<String, Object> map, Parcel dest, int flags) {
        dest.writeInt(map.size());

        for (Map.Entry<String, Object> entry : map.entrySet()) {
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

    public static HashMap<String, Object> createFromParcel(Parcel in) {
        final HashMap<String, Object> map = new HashMap<>();
        final int size = in.readInt();

        for (int i = 0; i < size; i ++) {
            final byte type = in.readByte();
            final String key = in.readString();
            switch (type) {
                case 1:
                    map.put(key, in.readInt());
                    break;
                case 2:
                    map.put(key, in.readLong());
                    break;
                case 3:
                    map.put(key, in.readFloat());
                    break;
                case 4:
                    map.put(key, in.readDouble());
                    break;
                case 5:
                    map.put(key, in.readString());
                    break;
                case 6:
                    map.put(key, in.readByte());
                    break;
                case 7:
                    map.put(key, in.readByte() == 1);
                    break;
            }
        }

        return map;
    }
}
