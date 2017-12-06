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

package cn.nekocode.hubs.ui.setting;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.nekocode.hubs.R;
import cn.nekocode.hubs.databinding.ItemConfigPropertyBinding;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class ConfigPropertyListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_PROPERTY = 0;
    private List<PropertyVO> mList;
    private UIEventListener mUIEventListener;


    public ConfigPropertyListAdapter(@NonNull List<PropertyVO> list) {
        this.mList = list;
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
                return new HubViewHolder(itemView);
        }

        throw new RuntimeException("Not supported viewtype: " + viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HubViewHolder) {
            ((HubViewHolder) holder).bind(mList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_PROPERTY;
    }

    private class HubViewHolder extends RecyclerView.ViewHolder {
        private ItemConfigPropertyBinding mBinding;


        HubViewHolder(View itemView) {
            super(itemView);

            mBinding = DataBindingUtil.bind(itemView);
        }

        @SuppressLint("SetTextI18n")
        void bind(PropertyVO vo) {
            mBinding.keyTv.setText(vo.getKey() + " = ");

            final PropertyEditText valueEt = mBinding.valueEt;
            valueEt.setVO(vo);
            valueEt.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus && mUIEventListener != null &&
                        !vo.getOldValue().equals(valueEt.getText().toString())) {

                    // Tell the listener to process the vo
                    mUIEventListener.onValueEdited(vo);
                    // Reset value
                    valueEt.resetText(vo.getValue());
                }
            });
        }
    }


    public interface UIEventListener {
        void onValueEdited(PropertyVO vo);
    }
}
