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

package cn.nekocode.hot.ui.browser;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.evernote.android.state.StateSaver;

import java.util.UUID;

import cn.nekocode.hot.HotApplication;
import cn.nekocode.hot.R;
import cn.nekocode.hot.base.BaseActivity;
import cn.nekocode.hot.data.model.Column;
import cn.nekocode.hot.databinding.ActivityBrowserBinding;
import cn.nekocode.hot.manager.base.BaseColumnManager;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class BrowserActivity extends BaseActivity {
    private static final String ARG_COLUMN = "column";
    private static final String ARG_URL = "url";
    private static final String SAVED_WEBVIEW = "SAVED_WEBVIEW";
    private ActivityBrowserBinding mBinding;
    private BaseColumnManager mColumnManager;
    private Column mColumn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StateSaver.restoreInstanceState(this, savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_browser);
        mColumnManager = HotApplication.getDefaultColumnManager(this);

        /*
          Get arguments from intent
         */
        final Intent intent = getIntent();
        if (intent == null) return;

        final String url;
        if (intent.hasExtra(ARG_URL)) {
            url = intent.getStringExtra(ARG_URL);
            mColumn = intent.getParcelableExtra(ARG_COLUMN);

        } else {
            if (intent.getData() == null) return;

            url = intent.getData().getQueryParameter("url");
            final String columnId = intent.getData().getQueryParameter("column_id");
            mColumnManager.readConfig(UUID.fromString(columnId))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(column -> {
                        mColumn = column;
                        // Save arguments to intent
                        intent.putExtra(ARG_URL, url);
                        intent.putExtra(ARG_COLUMN, column);

                    }, throwable -> {});
        }


        if (savedInstanceState == null) {
            mBinding.webView.loadUrl(url);

        } else {
            // Restore webview state
            mBinding.webView.restoreState(savedInstanceState.getBundle(SAVED_WEBVIEW));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        StateSaver.saveInstanceState(this, outState);

        // Save webview state
        final Bundle webviewState = new Bundle();
        mBinding.webView.saveState(webviewState);
        outState.putBundle(SAVED_WEBVIEW, webviewState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBinding.webView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.webView.onResume();
    }
}
