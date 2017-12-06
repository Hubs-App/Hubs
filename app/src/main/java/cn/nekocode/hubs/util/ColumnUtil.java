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

package cn.nekocode.hubs.util;

import android.support.annotation.NonNull;

import java.util.List;

import cn.nekocode.hubs.data.model.Column;
import cn.nekocode.hubs.data.model.ColumnPreference;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class ColumnUtil {

    public static int indexOfColumn(@NonNull List<Column> columns, @NonNull String columnId) {
        int index = 0;
        boolean finded = false;
        for (Column column : columns) {
            if (column.getId().equalsIgnoreCase(columnId)) {
                finded = true;
                break;
            }
            index++;
        }
        return finded ? index : -1;
    }

    public static int indexOfColumnPreference(@NonNull List<ColumnPreference> columnPreferences, @NonNull String columnId) {
        int index = 0;
        boolean finded = false;
        for (ColumnPreference columnPreference : columnPreferences) {
            if (columnPreference.getColumnId().equalsIgnoreCase(columnId)) {
                finded = true;
                break;
            }
            index++;
        }
        return finded ? index : -1;
    }

}
