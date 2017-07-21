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

package cn.nekocode.hot.ui.column;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

import cn.nekocode.hot.R;
import cn.nekocode.hot.data.model.Article;
import cn.nekocode.hot.databinding.ItemArticleBinding;
import cn.nekocode.hot.databinding.ItemBottomBinding;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class ArticleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ARTICLE = 0;
    private static final int TYPE_BOTTOMITEM = 1;
    private List<Article> mArticleList;
    private UIEventListener mUIEventListener;


    public ArticleListAdapter(@NonNull List<Article> articleList) {
        this.mArticleList = articleList;
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
            case TYPE_ARTICLE:
                itemView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_article, parent, false);
                return new ArticleViewHolder(itemView);

            case TYPE_BOTTOMITEM:
                itemView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_bottom, parent, false);
                return new BottomItemViewHolder(itemView);
        }

        throw new RuntimeException("Not supported viewtype: " + viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Article article = mArticleList.get(position);

        if (holder instanceof ArticleViewHolder) {
            ((ArticleViewHolder) holder).bind(article);

        } else if (holder instanceof BottomItemViewHolder) {
            ((BottomItemViewHolder) holder).bind((BottomItem) article);
        }
    }

    @Override
    public int getItemCount() {
        return mArticleList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            if (mArticleList.get(position) instanceof BottomItem) {
                return TYPE_BOTTOMITEM;
            } else {
                return TYPE_ARTICLE;
            }

        } else {
            return TYPE_ARTICLE;
        }
    }

    private class ArticleViewHolder extends RecyclerView.ViewHolder {
        private ItemArticleBinding mBinding;
        private Article mData;


        ArticleViewHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
            itemView.setOnClickListener(v -> {
                if (mUIEventListener != null && mData != null) {
                    mUIEventListener.onItemClicked(mData);
                }
            });
        }

        void bind(Article article) {
            mData = article;

            if (!TextUtils.isEmpty(article.getCoverUrl())) {
                Picasso.with(itemView.getContext())
                        .load(article.getCoverUrl())
                        .centerCrop()
                        .fit()
                        .into(mBinding.coverView);
            } else {
                mBinding.coverView.setImageDrawable(null);
            }

            mBinding.titleView.setText(article.getTitle());
            mBinding.descriptionView.setText(article.getDescription());
        }
    }

    private class BottomItemViewHolder extends RecyclerView.ViewHolder {
        private ItemBottomBinding mBinding;
        private BottomItem mData;


        BottomItemViewHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
            mBinding.button.setOnClickListener(v -> {
                v.setEnabled(false);

                if (mUIEventListener != null && mData != null) {
                    mUIEventListener.onBottomItemButtonClicked(mData.getState());
                }
            });
        }

        void bind(BottomItem bottomItem) {
            mData = bottomItem;

            if (bottomItem.isLoading()) {
                mBinding.button.setEnabled(false);
                mBinding.button.setText(R.string.loading);

            } else {
                mBinding.button.setEnabled(true);
                switch (bottomItem.getState()) {
                    case BottomItem.STATE_LOADMORE:
                        mBinding.button.setText(R.string.load_more);
                        break;

                    case BottomItem.STATE_RELOAD:
                        mBinding.button.setText(R.string.reload);
                        break;
                }
            }
        }
    }

    public interface UIEventListener {
        void onItemClicked(Article article);
        void onBottomItemButtonClicked(@BottomItem.State int state);
    }
}
