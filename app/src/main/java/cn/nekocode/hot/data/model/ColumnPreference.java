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

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class ColumnPreference implements Parcelable {
    private String columnId;
    private boolean isVisible;
    private int order;
    private Column column;


    public ColumnPreference() {
    }

    public ColumnPreference(Column column, boolean isVisible, int order) {
        this(column.getId(), isVisible, order);
        this.column = column;
    }

    public ColumnPreference(String columnId, boolean isVisible, int order) {
        this.columnId = columnId;
        this.isVisible = isVisible;
        this.order = order;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Column getColumn() {
        return column;
    }

    public void setColumn(Column column) {
        this.column = column;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.columnId);
        dest.writeByte(this.isVisible ? (byte) 1 : (byte) 0);
        dest.writeInt(this.order);
        dest.writeParcelable(this.column, flags);
    }

    protected ColumnPreference(Parcel in) {
        this.columnId = in.readString();
        this.isVisible = in.readByte() != 0;
        this.order = in.readInt();
        this.column = in.readParcelable(Column.class.getClassLoader());
    }

    public static final Creator<ColumnPreference> CREATOR = new Creator<ColumnPreference>() {
        @Override
        public ColumnPreference createFromParcel(Parcel source) {
            return new ColumnPreference(source);
        }

        @Override
        public ColumnPreference[] newArray(int size) {
            return new ColumnPreference[size];
        }
    };
}
