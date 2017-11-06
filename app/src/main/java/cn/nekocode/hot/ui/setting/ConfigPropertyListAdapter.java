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

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.nekocode.hot.R;
import cn.nekocode.hot.databinding.ItemConfigPropertyBinding;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class ConfigPropertyListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_PROPERTY = 0;
    private List<Pair<String, Object>> mProperties;
    private UIEventListener mUIEventListener;


    public ConfigPropertyListAdapter(@NonNull List<Pair<String, Object>> properties) {
        this.mProperties = properties;
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
            case TYPE_PROPERTY:
                itemView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_config_property, parent, false);
                return new ColumnViewHolder(itemView);
        }

        throw new RuntimeException("Not supported viewtype: " + viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Pair<String, Object> entry = mProperties.get(position);

        if (holder instanceof ColumnViewHolder) {
            ((ColumnViewHolder) holder).bind(entry.first, entry.second);
        }
    }

    @Override
    public int getItemCount() {
        return mProperties.size();
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_PROPERTY;
    }

    private class ColumnViewHolder extends RecyclerView.ViewHolder {
        private ItemConfigPropertyBinding mBinding;


        ColumnViewHolder(View itemView) {
            super(itemView);

            mBinding = DataBindingUtil.bind(itemView);
        }

        @SuppressLint("SetTextI18n")
        void bind(String key, Object value) {
            mBinding.keyTv.setText(key + " = ");

            if (value instanceof String) {
                mBinding.valueEt.setText("\"" + value + "\"");

            } else if (value instanceof Integer || value instanceof Long ||
                    value instanceof Float || value instanceof Double || value instanceof Byte) {
                mBinding.valueEt.setText(String.valueOf(value));

            } else if (value instanceof Boolean) {
                mBinding.valueEt.setText(((Boolean) value) ? "true" : "false");
            }
        }
    }

    public interface UIEventListener {
    }
}
