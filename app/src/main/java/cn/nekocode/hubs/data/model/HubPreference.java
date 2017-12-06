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

package cn.nekocode.hubs.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class HubPreference implements Parcelable {
    private String hubId;
    private boolean isVisible;
    private int order;
    private Hub hub;


    public HubPreference() {
    }

    public HubPreference(Hub hub, boolean isVisible, int order) {
        this(hub.getId(), isVisible, order);
        this.hub = hub;
    }

    public HubPreference(String hubId, boolean isVisible, int order) {
        this.hubId = hubId;
        this.isVisible = isVisible;
        this.order = order;
    }

    public String getHubId() {
        return hubId;
    }

    public void setHubId(String hubId) {
        this.hubId = hubId;
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

    public Hub getHub() {
        return hub;
    }

    public void setHub(Hub hub) {
        this.hub = hub;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.hubId);
        dest.writeByte(this.isVisible ? (byte) 1 : (byte) 0);
        dest.writeInt(this.order);
        dest.writeParcelable(this.hub, flags);
    }

    protected HubPreference(Parcel in) {
        this.hubId = in.readString();
        this.isVisible = in.readByte() != 0;
        this.order = in.readInt();
        this.hub = in.readParcelable(Hub.class.getClassLoader());
    }

    public static final Creator<HubPreference> CREATOR = new Creator<HubPreference>() {
        @Override
        public HubPreference createFromParcel(Parcel source) {
            return new HubPreference(source);
        }

        @Override
        public HubPreference[] newArray(int size) {
            return new HubPreference[size];
        }
    };
}
