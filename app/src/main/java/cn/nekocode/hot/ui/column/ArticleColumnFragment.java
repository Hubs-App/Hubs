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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.evernote.android.state.State;
import com.evernote.android.state.StateSaver;

import java.util.ArrayList;

import cn.nekocode.hot.R;
import cn.nekocode.hot.base.BaseColumnFragment;
import cn.nekocode.hot.data.model.Article;
import cn.nekocode.hot.data.model.Column;
import cn.nekocode.hot.databinding.FragmentArticleColumnBinding;
import cn.nekocode.hot.luaj.ColumnLuaBridge;
import cn.nekocode.hot.util.DividerItemDecoration;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class ArticleColumnFragment extends BaseColumnFragment implements SwipeRefreshLayout.OnRefreshListener {
    private FragmentArticleColumnBinding mBinding;
    @State
    public ArrayList<Article> mArticleList;
    private ArticleListAdapter mAdapter;
    @State
    public int mPage;

    private Column mColumn;
    private ColumnLuaBridge mLuaBridge;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StateSaver.restoreInstanceState(this, savedInstanceState);
        mColumn = getColumnFromBundle(getArguments());

        if (mArticleList == null) {
            mArticleList = new ArrayList<>();
        }

        mAdapter = new ArticleListAdapter(mArticleList);
        mAdapter.setUIEventListener(new ArticleListAdapter.UIEventListener() {
            @Override
            public void onItemClicked(Article article) {

            }

            @Override
            public void onBottomItemButtonClicked(@BottomItem.State int state) {
                switch (state) {
                    case BottomItem.STATE_LOADMORE:
                        onLoadMore(mPage + 1);
                        break;
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        StateSaver.saveInstanceState(this, outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_article_column, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mBinding.recyclerView.setAdapter(mAdapter);
        mBinding.recyclerView.addItemDecoration(DividerItemDecoration.obtainDefault(getContext()));

        mBinding.refreshLayout.setOnRefreshListener(this);
        if (mArticleList.size() == 0) {
            mBinding.refreshLayout.setRefreshing(true);
            onRefresh();
        }
    }

    @Override
    public void onRefresh() {
        Observable.<ArrayList<Article>>create(emitter -> {
            emitter.onNext(getColumnLuaBridge().getArticles(0));
            emitter.onComplete();
        })
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(articles -> {
                    mPage = 0;

                    if (articles.size() > 0) {
                        mArticleList.clear();
                        mArticleList.addAll(articles);
                        mArticleList.add(new BottomItem(BottomItem.STATE_LOADMORE));
                        mAdapter.notifyDataSetChanged();

                    } else {
                        // TODO
                    }

                    mBinding.refreshLayout.setRefreshing(false);
                });
    }

    private void onLoadMore(final int page) {
        // TODO 设置按钮为加载状态（不可点击）

        Observable.<ArrayList<Article>>create(emitter -> {
            emitter.onNext(getColumnLuaBridge().getArticles(page));
            emitter.onComplete();
        })
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(articles -> {
                    mPage = page;

                    final int oldSize = mArticleList.size();
                    if (articles.size() > 0) {
                        final Article bottomItem = mArticleList.remove(oldSize - 1);
                        mArticleList.addAll(articles);
                        mArticleList.add(bottomItem);
                        mAdapter.notifyDataSetChanged();

                    } else {
                        mArticleList.remove(oldSize - 1);
                        mAdapter.notifyItemRemoved(oldSize - 1);
                    }

                    // TODO 重设按钮状态
                });
    }

    private ColumnLuaBridge getColumnLuaBridge() {
        if (mLuaBridge == null) {
            mLuaBridge = ColumnLuaBridge.load(getContext(), mColumn);
        }
        return mLuaBridge;
    }
}
