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

package cn.nekocode.hubs;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;

import cn.nekocode.hubs.manager.FileManager;
import cn.nekocode.hubs.manager.HubManager;
import cn.nekocode.hubs.manager.PreferenceManager;
import cn.nekocode.hubs.manager.base.BaseFileManager;
import cn.nekocode.hubs.manager.base.BaseHubManager;
import cn.nekocode.hubs.manager.base.BasePreferenceManager;
import io.fabric.sdk.android.Fabric;
import okhttp3.OkHttpClient;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class HubsApplication extends Application {
    private OkHttpClient mDefaultOkHttpClient;
    private BaseFileManager mDefaultFileManager;
    private BaseHubManager mDefaultHubManager;
    private BasePreferenceManager mDefaultPreferenceManager;


    @NonNull
    public static OkHttpClient getDefaultOkHttpClient(Context context) {
        return ((HubsApplication) (context.getApplicationContext()))
                .mDefaultOkHttpClient;
    }

    @NonNull
    public static BaseFileManager getDefaultFileManager(Context context) {
        return ((HubsApplication) (context.getApplicationContext()))
                .mDefaultFileManager;
    }

    @NonNull
    public static BaseHubManager getDefaultHubManager(Context context) {
        return ((HubsApplication) (context.getApplicationContext()))
                .mDefaultHubManager;
    }

    @NonNull
    public static BasePreferenceManager getDefaultPreferenceManager(Context context) {
        return ((HubsApplication) (context.getApplicationContext()))
                .mDefaultPreferenceManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }

        /*
          Init default vars
         */
        mDefaultOkHttpClient = new OkHttpClient();

        /*
          Setup managers
         */
        mDefaultFileManager = new FileManager();
        mDefaultHubManager = new HubManager(mDefaultFileManager);
        mDefaultPreferenceManager = new PreferenceManager(this);
    }
}
