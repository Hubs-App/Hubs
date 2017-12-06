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

package cn.nekocode.hubs.ui.hub;

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
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.util.ArrayList;

import cn.nekocode.hubs.ActivityRouter;
import cn.nekocode.hubs.R;
import cn.nekocode.hubs.base.BaseHubFragment;
import cn.nekocode.hubs.data.model.Article;
import cn.nekocode.hubs.databinding.FragmentArticleHubBinding;
import cn.nekocode.hubs.luaj.EntryLuaBridge;
import cn.nekocode.hubs.util.DividerItemDecoration;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class ArticleHubFragment extends BaseHubFragment implements SwipeRefreshLayout.OnRefreshListener {
    private FragmentArticleHubBinding mBinding;
    @State
    public ArrayList<Article> mArticleList;
    private BottomItem mBottomItem;
    private ArticleListAdapter mAdapter;
    @State
    public int mPage;

    private EntryLuaBridge mLuaBridge;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StateSaver.restoreInstanceState(this, savedInstanceState);
        mLuaBridge = EntryLuaBridge.create(getContext(), getHub());

        /*
          Data initialize
         */
        if (mArticleList == null) {
            mArticleList = new ArrayList<>();

        } else {
            // If the data is restored from savedInstanceState
            final int size = mArticleList.size();
            if (size > 0) {
                final Article bottomItem = mArticleList.get(size - 1);
                if (bottomItem instanceof BottomItem) {
                    mBottomItem = ((BottomItem) bottomItem);
                    mBottomItem.setLoading(false);
                }
            }
        }

        /*
          Adapter initialize
         */
        mAdapter = new ArticleListAdapter(mArticleList);
        mAdapter.setUIEventListener(new ArticleListAdapter.UIEventListener() {
            @Override
            public void onItemClicked(Article article) {
                ActivityRouter.IMPL.gotoBrowser(getContext(), getHub(), article.getUrl());
            }

            @Override
            public void onBottomItemButtonClicked(@BottomItem.State int state) {
                switch (state) {
                    case BottomItem.STATE_LOADMORE:
                        onLoadMore(mPage + 1);
                        break;

                    case BottomItem.STATE_RELOAD:
                        onLoadMore(mPage);
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

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_article_hub, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*
          Setup the recyclerview
         */
        mBinding.recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mBinding.recyclerView.setAdapter(mAdapter);
        mBinding.recyclerView.setItemAnimator(null);
        mBinding.recyclerView.addItemDecoration(DividerItemDecoration.obtainDefault(getContext()));

        /*
          Setup the refreshlayout
         */
        mBinding.refreshLayout.setOnRefreshListener(this);
        if (mArticleList.size() == 0) {
            doFirstLoad(() -> {
                mBinding.refreshLayout.setRefreshing(true);
                onRefresh();
            });
        }
    }

    @Override
    public void onRefresh() {
        mPage = 0;

        mLuaBridge.getArticles(0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .to(AutoDispose.with(AndroidLifecycleScopeProvider.from(this)).forSingle())
                .subscribe(articles -> {
                    mBottomItem = null;
                    mArticleList.clear();

                    if (articles.size() > 0) {
                        // Add article items
                        mArticleList.addAll(articles);

                        // Add loadmore item
                        mBottomItem = new BottomItem(BottomItem.STATE_LOADMORE);
                        mArticleList.add(mBottomItem);
                    }
                    mAdapter.notifyDataSetChanged();

                    mBinding.refreshLayout.setRefreshing(false);

                }, throwable -> {
                    mArticleList.clear();

                    // Add reload item
                    mBottomItem = new BottomItem(BottomItem.STATE_RELOAD);
                    mArticleList.add(mBottomItem);
                    mAdapter.notifyDataSetChanged();

                    mBinding.refreshLayout.setRefreshing(false);

                    showMessageIfInDebug(throwable.getMessage());
                });
    }

    private void onLoadMore(final int page) {
        mPage = page;

        mBottomItem.setLoading(true);
        mAdapter.notifyItemChanged(mArticleList.size() - 1);

        mLuaBridge.getArticles(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .to(AutoDispose.with(AndroidLifecycleScopeProvider.from(this)).forSingle())
                .subscribe(articles -> {
                    final int oldSize = mArticleList.size();
                    if (articles.size() > 0) {
                        mBottomItem = (BottomItem) mArticleList.remove(oldSize - 1);
                        mBottomItem.setLoading(false);
                        mBottomItem.setState(BottomItem.STATE_LOADMORE);
                        mArticleList.addAll(articles);
                        mArticleList.add(mBottomItem);
                        mAdapter.notifyDataSetChanged();

                    } else {
                        mBottomItem = null;
                        mArticleList.remove(oldSize - 1);
                        mAdapter.notifyItemRemoved(oldSize - 1);
                    }

                }, throwable -> {
                    mBottomItem.setState(BottomItem.STATE_RELOAD);
                    mBottomItem.setLoading(false);
                    mAdapter.notifyItemChanged(mArticleList.size() - 1);

                    showMessageIfInDebug(throwable.getMessage());
                });
    }
}
