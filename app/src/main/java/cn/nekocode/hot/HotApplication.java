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

package cn.nekocode.hot;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;

import cn.nekocode.hot.manager.FileManager;
import cn.nekocode.hot.manager.InstallManager;
import cn.nekocode.hot.manager.base.BaseFileManager;
import cn.nekocode.hot.manager.base.BaseInstallManager;
import io.fabric.sdk.android.Fabric;
import okhttp3.OkHttpClient;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class HotApplication extends Application {
    private OkHttpClient mDefaultOkHttpClient;
    private BaseFileManager mDefaultFileManager;
    private BaseInstallManager mDefaultInstallManager;


    @NonNull
    public static OkHttpClient getDefaultOkHttpClient(Context context) {
        return ((HotApplication) (context.getApplicationContext()))
                .mDefaultOkHttpClient;
    }

    @NonNull
    public static BaseFileManager getDefaultFileManager(Context context) {
        return ((HotApplication) (context.getApplicationContext()))
                .mDefaultFileManager;
    }

    @NonNull
    public static BaseInstallManager getDefaultInstallManager(Context context) {
        return ((HotApplication) (context.getApplicationContext()))
                .mDefaultInstallManager;
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
        mDefaultInstallManager = new InstallManager(mDefaultFileManager);
    }
}
