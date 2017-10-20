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
import org.luaj.vm2.compiler.LuaC;

import java.util.UUID;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class Column implements Parcelable {
    public static final String TYPE_ARTICLE = "article";

    private UUID id;
    private String name;
    private String type;
    private String version;
    private String entry;


    public static Column fromLua(String luaText) {
        final Globals globals = new Globals();
        LoadState.install(globals);
        LuaC.install(globals);

        return fromLua(luaText, globals);
    }

    public static Column fromLua(String luaText, Globals globals) {
        globals.load(luaText).call();

        final Column column = new Column();
        column.setId(UUID.fromString(globals.get("uuid").checkjstring()));
        column.setName(globals.get("name").checkjstring());
        column.setType(globals.get("type").checkjstring());
        column.setVersion(globals.get("version").checkjstring());
        column.setEntry(globals.get("entry").checkjstring());

        return column;
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
    }

    public Column() {
    }

    protected Column(Parcel in) {
        this.id = (UUID) in.readSerializable();
        this.name = in.readString();
        this.type = in.readString();
        this.version = in.readString();
        this.entry = in.readString();
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
