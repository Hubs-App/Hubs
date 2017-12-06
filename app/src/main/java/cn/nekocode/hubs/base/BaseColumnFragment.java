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

package cn.nekocode.hubs.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import cn.nekocode.hubs.data.model.Column;
import cn.nekocode.hubs.ui.column.ArticleColumnFragment;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public abstract class BaseColumnFragment extends BaseLazyLoadFragment {
    public static final String ARG_COLUMN = "column";

    private Column mColumn;


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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mColumn = getArguments().getParcelable(ARG_COLUMN);
        if (mColumn == null) throw new RuntimeException("Get column from arguments failed.");
    }

    public Column getColumn() {
        return mColumn;
    }

    public void showMessageIfInDebug(@NonNull String message) {
        if (mColumn.isDebug()) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
