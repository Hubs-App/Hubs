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

package cn.nekocode.hot.base;

import android.os.Bundle;
import android.support.annotation.NonNull;

import cn.nekocode.hot.data.model.Column;
import cn.nekocode.hot.ui.column.ArticleColumnFragment;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public abstract class BaseColumnFragment extends BaseFragment {
    public static final String ARG_COLUMN = "column";

    @NonNull
    public static BaseColumnFragment newInstance(@NonNull Column column) {
        final String columnType = column.getType();
        BaseColumnFragment fragment;

        switch (columnType) {
            case Column.TYPE_ARTICLE:
            default:
                fragment = new ArticleColumnFragment();
                break;
        }

        final Bundle args = new Bundle();
        args.putParcelable(ARG_COLUMN, column);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    public static Column getColumnFromBundle(@NonNull Bundle bundle) {
        final Column column = bundle.getParcelable(ARG_COLUMN);
        if (column == null) throw new RuntimeException("getColumnFromBundle() failed.");
        return column;
    }
}
