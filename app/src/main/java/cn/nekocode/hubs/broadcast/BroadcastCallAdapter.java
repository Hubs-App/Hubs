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

package cn.nekocode.hubs.broadcast;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import cn.nekocode.meepo.CallMethod;
import cn.nekocode.meepo.MeepoUtils;
import cn.nekocode.meepo.adapter.CallAdapter;
import cn.nekocode.meepo.config.Config;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class BroadcastCallAdapter implements CallAdapter<Void> {

    @Nullable
    @Override
    public Void call(@NonNull Config config, @NonNull CallMethod method, @NonNull Object[] args) {
        final Context context = MeepoUtils.getContextFromFirstParameter(args);
        if (context == null) {
            return null;
        }

        final Intent intent = new Intent();
        intent.setAction(method.getAction());
        intent.putExtras(method.getBundle(args));
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

        return null;
    }
}
