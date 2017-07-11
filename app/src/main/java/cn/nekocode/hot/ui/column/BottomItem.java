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

package cn.nekocode.hot.ui.column;

import android.os.Parcel;
import android.support.annotation.IntDef;

import cn.nekocode.hot.data.model.Article;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class BottomItem extends Article {
    public static final int STATE_LOADMORE = 0;
    public static final int STATE_RELOAD = 1;

    @IntDef({STATE_LOADMORE, STATE_RELOAD})
    @interface State {}

    @State
    private int state;
    private boolean isLoading;


    public BottomItem(@State int state) {
        this.state = state;
        this.isLoading = false;
    }

    @State
    public int getState() {
        return state;
    }

    public void setState(@State int state) {
        this.state = state;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.state);
    }

    protected BottomItem(Parcel in) {
        super(in);
        this.state = in.readInt();
    }

    public static final Creator<BottomItem> CREATOR = new Creator<BottomItem>() {
        @Override
        public BottomItem createFromParcel(Parcel source) {
            return new BottomItem(source);
        }

        @Override
        public BottomItem[] newArray(int size) {
            return new BottomItem[size];
        }
    };
}
