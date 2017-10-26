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

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public interface Constants {
    String ACTION_PREFIX = "cn.nekocode.hot.action.";
    String ACTION_NOTIFY_COLUMN_INSTALLED = ACTION_PREFIX + "NOTIFY_COLUMN_INSTALLED";
    String ACTION_NOTIFY_COLUMN_UNINSTALLED = ACTION_PREFIX + "NOTIFY_COLUMN_UNINSTALLED";
    String ACTION_NOTIFY_COLUMN_PREFERENCE_CHANGED = ACTION_PREFIX + "NOTIFY_COLUMN_PREFERENCE_CHANGED";

    /**
     * You can run the following command to refresh a column page immediately.
     * adb shell "am broadcast -a cn.nekocode.hot.action.NOTIFY_COLUMN_CONFIG_CHANGED -e column_id '$column_id'"
     */
    String ACTION_NOTIFY_COLUMN_CONFIG_CHANGED = ACTION_PREFIX + "NOTIFY_COLUMN_CONFIG_CHANGED";


    String ARG_COLUMNS = "columns";
    String ARG_COLUMNID = "column_id";
}
