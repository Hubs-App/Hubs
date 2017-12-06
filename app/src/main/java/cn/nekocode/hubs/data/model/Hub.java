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

import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.compiler.LuaC;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class Hub implements Parcelable {
    public static final String TYPE_ARTICLE = "article";
    public static final String[] SUPPORTED_TYPES = new String[]{TYPE_ARTICLE};

    private String id;
    private String name;
    private String type;
    private String version;
    private String entry;
    private String browser;
    private boolean debug = false;
    private UserConfig userConfig = new UserConfig();


    /**
     * Create hub from a lua script text
     */
    public static Hub fromLua(String luaText) throws Exception {
        // Obatain a lua globals for loading configs
        final Globals globals = new Globals();
        LoadState.install(globals);
        LuaC.install(globals);

        final Hub hub = new Hub();
        globals.load(luaText).call();

        Varargs pair;
        LuaValue key = LuaValue.NIL;
        LuaValue value;
        String keyStr;
        while (true) {
            pair = globals.next(key);
            if ((key = pair.arg1()).isnil())
                break;

            value = pair.arg(2);
            keyStr = key.checkjstring();
            switch (keyStr) {
                case "ID":
                    hub.setId(value.checkjstring().toLowerCase());
                    break;

                case "NAME":
                    hub.setName(value.checkjstring());
                    break;

                case "TYPE":
                    final String type = value.checkjstring();
                    boolean isTypeSupported = false;
                    for (String supportedType : SUPPORTED_TYPES) {
                        if (supportedType.equalsIgnoreCase(type)) {
                            isTypeSupported = true;
                            break;
                        }
                    }

                    if (isTypeSupported) {
                        hub.setType(type);
                    } else {
                        throw new Exception("Not supported hub type.");
                    }
                    break;

                case "VERSION":
                    hub.setVersion(value.checkjstring());
                    break;

                case "ENTRY":
                    hub.setEntry(value.checkjstring());
                    break;

                case "BROWSER":
                    hub.setBrowser(value.checkjstring());
                    break;

                case "DEBUG":
                    hub.setDebug(value.checkboolean());
                    break;

                default:
                    hub.userConfig.put(keyStr, value);
                    break;
            }
        }

        hub.checkProperties();

        return hub;
    }

    /**
     * Set all key-value pairs to lua globals
     */
    public void setAllTo(Globals globals) {
        globals.set("ID", id);
        globals.set("NAME", name);
        globals.set("TYPE", type);
        globals.set("VERSION", version);
        globals.set("ENTRY", entry);
        if (browser != null) globals.set("BROWSER", browser);
        globals.set("DEBUG", debug ? LuaValue.TRUE : LuaValue.FALSE);
        userConfig.setAllTo(globals);
    }

    /**
     * Check if all necessary properties is assigned
     */
    public void checkProperties() throws Exception {
        if (id == null || name == null ||type == null || version == null || entry == null) {
            throw new Exception("Not all necessary properties in the hub have been assigned.");
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public UserConfig getUserConfig() {
        return userConfig;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.type);
        dest.writeString(this.version);
        dest.writeString(this.entry);
        dest.writeString(this.browser);
        dest.writeByte(this.debug ? (byte) 1 : 0);
        dest.writeParcelable(this.userConfig, flags);
    }

    public Hub() {
    }

    protected Hub(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.type = in.readString();
        this.version = in.readString();
        this.entry = in.readString();
        this.browser = in.readString();
        this.debug = (in.readByte() == 1);
        this.userConfig.clear();
        this.userConfig.putAll(in.readParcelable(UserConfig.class.getClassLoader()));
    }

    public static final Creator<Hub> CREATOR = new Creator<Hub>() {
        @Override
        public Hub createFromParcel(Parcel source) {
            return new Hub(source);
        }

        @Override
        public Hub[] newArray(int size) {
            return new Hub[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (getId() != null && obj instanceof Hub) {
            return getId().equalsIgnoreCase(((Hub) obj).getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}
