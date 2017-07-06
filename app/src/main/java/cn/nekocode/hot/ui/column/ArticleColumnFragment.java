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
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
public class ArticleColumnFragment extends BaseColumnFragment {
    private FragmentArticleColumnBinding mBinding;
    private Column mColumn;
    private ArrayList<Article> mArticleList = new ArrayList<>();
    private ArticleListAdapter mAdapter;
    private ColumnLuaBridge mLuaBridge;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mColumn = getColumnFromBundle(getArguments());
        mLuaBridge = ColumnLuaBridge.load(getContext(), mColumn);

        if (mArticleList == null) {
            mArticleList = new ArrayList<>();
        }

        mAdapter = new ArticleListAdapter(mArticleList);

        Observable.<ArrayList<Article>>create(emitter -> {
            emitter.onNext(mLuaBridge.getArticles(0));
            emitter.onComplete();
        })
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(articles -> {
                    mArticleList.clear();
                    mArticleList.addAll(articles);
                    mAdapter.notifyDataSetChanged();
                });
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
    }
}
