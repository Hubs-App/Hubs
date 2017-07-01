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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

import cn.nekocode.hot.R;
import cn.nekocode.hot.data.model.Article;
import cn.nekocode.hot.databinding.ItemArticleBinding;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class ArticleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ARTICLE = 0;
    private List<Article> mArticleList;


    public ArticleListAdapter(@NonNull List<Article> articleList) {
        this.mArticleList = articleList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ARTICLE:
                final View itemView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_article, parent, false);
                return new ArticleViewHolder(itemView);
        }

        throw new RuntimeException("Not supported viewtype: " + viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ArticleViewHolder) {
            ((ArticleViewHolder) holder).bind(mArticleList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mArticleList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ARTICLE;
    }

    private static class ArticleViewHolder extends RecyclerView.ViewHolder {
        private ItemArticleBinding mBinding;


        ArticleViewHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
        }

        public void bind(Article article) {
            Picasso.with(itemView.getContext())
                    .load(article.getCoverUrl())
                    .centerCrop()
                    .fit()
                    .into(mBinding.imageView);

            mBinding.textView.setText(article.getTitle());
        }
    }
}
