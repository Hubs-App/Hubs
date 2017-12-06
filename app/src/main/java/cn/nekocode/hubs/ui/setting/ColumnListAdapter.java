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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.nekocode.hubs.R;
import cn.nekocode.hubs.data.model.ColumnPreference;
import cn.nekocode.hubs.databinding.ItemColumnBinding;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class ColumnListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_COLUMN = 0;
    private List<ColumnPreference> mColumnPreferenceList;
    private UIEventListener mUIEventListener;
    private final ItemTouchHelper mItemTouchHelper;


    public ColumnListAdapter(@NonNull List<ColumnPreference> columnList) {
        this.mColumnPreferenceList = columnList;

        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.UP | ItemTouchHelper.DOWN);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {

                final int i = viewHolder.getAdapterPosition();
                final int j = target.getAdapterPosition();
                final ColumnPreference l = mColumnPreferenceList.get(i);
                final ColumnPreference r = mColumnPreferenceList.get(j);
                mColumnPreferenceList.set(i, mColumnPreferenceList.set(j, l));
                l.setOrder(j);
                r.setOrder(i);

                notifyItemMoved(i, j);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                if (mUIEventListener != null) {
                    mUIEventListener.onItemsSwapped();
                }
            }
        });
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
        final ColumnPreference preference = mColumnPreferenceList.get(position);

        if (holder instanceof ColumnViewHolder) {
            ((ColumnViewHolder) holder).bind(preference);
        }
    }

    @Override
    public int getItemCount() {
        return mColumnPreferenceList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_COLUMN;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private class ColumnViewHolder extends RecyclerView.ViewHolder {
        private ItemColumnBinding mBinding;
        private ColumnPreference mPreference;


        @SuppressLint("ClickableViewAccessibility")
        ColumnViewHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);

            mBinding.reorderBtn.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mItemTouchHelper.startDrag(ColumnViewHolder.this);
                        return true;
                }
                return false;
            });

            mBinding.configBtn.setOnClickListener(v -> {
                if (mPreference != null && mUIEventListener != null) {
                    mUIEventListener.onItemConfigButtonClick(getAdapterPosition(), mPreference);
                }
            });

            mBinding.visibilityBtn.setOnClickListener(v -> {
                if (mPreference != null) {
                    mPreference.setVisible(!mPreference.isVisible());
                    mBinding.visibilityBtn.setText(mPreference.isVisible() ? "Visible" : "Invisible");

                    if (mUIEventListener != null) {
                        mUIEventListener.onItemVisibilityButtonClick(getAdapterPosition(), mPreference);
                    }
                }
            });

            mBinding.uninstallBtn.setOnClickListener(v -> {
                if (mPreference != null && mUIEventListener != null) {
                    mUIEventListener.onItemUninstallButtonClick(getAdapterPosition(), mPreference);
                }
            });
        }

        void bind(ColumnPreference preference) {
            mPreference = preference;
            mBinding.titleTv.setText(preference.getColumn().getName());
            mBinding.descriptionTv.setText(preference.getColumn().getVersion());
            mBinding.visibilityBtn.setText(mPreference.isVisible() ? "Visible" : "Invisible"); // TODO
        }
    }

    public interface UIEventListener {
        void onItemsSwapped();
        void onItemConfigButtonClick(int position, ColumnPreference preference);
        void onItemVisibilityButtonClick(int position, ColumnPreference preference);
        void onItemUninstallButtonClick(int position, ColumnPreference preference);
    }
}
