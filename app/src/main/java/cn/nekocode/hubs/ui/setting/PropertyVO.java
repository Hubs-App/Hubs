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

package cn.nekocode.hubs.ui.setting;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class PropertyVO implements Parcelable {
    private String key;
    private String value;
    private String oldValue;
    private int selectionStart;
    private int selectionEnd;
    private boolean isFoucused;


    public PropertyVO(
            String key, String value, String oldValue,
            int selectionStart, int selectionEnd, boolean isFoucused) {

        this.key = key;
        this.value = value;
        this.oldValue = oldValue;
        this.selectionStart = selectionStart;
        this.selectionEnd = selectionEnd;
        this.isFoucused = isFoucused;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public int getSelectionStart() {
        return selectionStart;
    }

    public void setSelectionStart(int selectionStart) {
        this.selectionStart = selectionStart;
    }

    public int getSelectionEnd() {
        return selectionEnd;
    }

    public void setSelectionEnd(int selectionEnd) {
        this.selectionEnd = selectionEnd;
    }

    public boolean isFoucused() {
        return isFoucused;
    }

    public void setFoucused(boolean foucused) {
        isFoucused = foucused;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeString(this.value);
        dest.writeString(this.oldValue);
        dest.writeInt(this.selectionStart);
        dest.writeInt(this.selectionEnd);
        dest.writeByte(this.isFoucused ? (byte) 1 : (byte) 0);
    }

    protected PropertyVO(Parcel in) {
        this.key = in.readString();
        this.value = in.readString();
        this.oldValue = in.readString();
        this.selectionStart = in.readInt();
        this.selectionEnd = in.readInt();
        this.isFoucused = in.readByte() != 0;
    }

    public static final Creator<PropertyVO> CREATOR = new Creator<PropertyVO>() {
        @Override
        public PropertyVO createFromParcel(Parcel source) {
            return new PropertyVO(source);
        }

        @Override
        public PropertyVO[] newArray(int size) {
            return new PropertyVO[size];
        }
    };
}
