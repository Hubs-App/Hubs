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

package cn.nekocode.hot.ui.setting;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.nekocode.hot.R;
import cn.nekocode.hot.data.model.Column;
import cn.nekocode.hot.databinding.ItemColumnBinding;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class ColumnListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_COLUMN = 0;
    private List<Column> mColumnList;
    private UIEventListener mUIEventListener;


    public ColumnListAdapter(@NonNull List<Column> columnList) {
        this.mColumnList = columnList;
    }

    public UIEventListener getUIEventListener() {
        return mUIEventListener;
    }

    public void setUIEventListener(UIEventListener mUIEventListener) {
        this.mUIEventListener = mUIEventListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case TYPE_COLUMN:
                itemView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_column, parent, false);
                return new ColumnViewHolder(itemView);
        }

        throw new RuntimeException("Not supported viewtype: " + viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Column column = mColumnList.get(position);

        if (holder instanceof ColumnViewHolder) {
            ((ColumnViewHolder) holder).bind(column);
        }
    }

    @Override
    public int getItemCount() {
        return mColumnList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_COLUMN;
    }

    private class ColumnViewHolder extends RecyclerView.ViewHolder {
        private ItemColumnBinding mBinding;


        ColumnViewHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
        }

        void bind(Column column) {
        }
    }

    public interface UIEventListener {
    }
}
