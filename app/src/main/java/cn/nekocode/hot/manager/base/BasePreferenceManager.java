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

package cn.nekocode.hot.manager.base;

import android.support.annotation.NonNull;

import java.util.ArrayList;

import cn.nekocode.hot.data.model.Column;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public abstract class BasePreferenceManager {

    /**
     * Load ordered column list
     */
    public abstract ArrayList<Column> loadOrderedColumns(@NonNull BaseFileManager fileManager);

    /**
     * Save ordered column list
     */
    public abstract void saveOrderedColumns(ArrayList<Column> columns);
}
