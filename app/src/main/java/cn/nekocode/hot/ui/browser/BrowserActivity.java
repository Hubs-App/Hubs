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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.webkit.WebView;
import android.widget.Toast;

import com.evernote.android.state.StateSaver;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.util.ArrayList;
import java.util.UUID;

import cn.nekocode.hot.Constants;
import cn.nekocode.hot.HotApplication;
import cn.nekocode.hot.R;
import cn.nekocode.hot.base.BaseActivity;
import cn.nekocode.hot.data.model.Column;
import cn.nekocode.hot.databinding.ActivityBrowserBinding;
import cn.nekocode.hot.luaj.BrowserLuaBridge;
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
    private final BroadcastReceiver mBroadcastReceiver = new BrowserBroadcastReceiver();
    @Nullable
    private Column mColumn;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StateSaver.restoreInstanceState(this, savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_browser);
        mColumnManager = HotApplication.getDefaultColumnManager(this);

        /*
          Get arguments from intent
         */
        final Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        if (intent.hasExtra(ARG_COLUMN)) {
            mColumn = intent.getParcelableExtra(ARG_COLUMN);
            setupWebView(savedInstanceState, mColumn);

        } else {
            final String columnId;
            if (intent.getData() != null && !TextUtils.isEmpty(
                    columnId = intent.getData().getQueryParameter("column_id"))) {

                mColumnManager.readConfig(UUID.fromString(columnId))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .to(AutoDispose.with(AndroidLifecycleScopeProvider.from(this)).forSingle())
                        .subscribe(column -> {
                            setupWebView(savedInstanceState, column);

                        }, throwable -> {
                            setupWebView(savedInstanceState, null);
                            showMessageIfInDebug(throwable.getMessage());
                        });

            } else {
                setupWebView(savedInstanceState, null);
            }
        }

        /*
          Register broadcast receiver
         */
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_NOTIFY_COLUMN_UNINSTALLED);
        intentFilter.addAction(Constants.ACTION_NOTIFY_COLUMN_PREFERENCE_CHANGED);
        intentFilter.addAction(Constants.ACTION_NOTIFY_COLUMN_CONFIG_CHANGED);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mBroadcastReceiver, intentFilter);
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private void setupWebView(Bundle savedInstanceState, Column column) {
        final Intent intent = getIntent();
        // Save to intent
        intent.putExtra(ARG_COLUMN, column);
        mColumn = column;

        if (column != null) {
            final BrowserLuaBridge luaBridge = BrowserLuaBridge.create(this, column);
            if (luaBridge == null) {
                mBinding.webView.setCallback(null);

            } else {
                mBinding.webView.setCallback(new WebViewCallback() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        luaBridge.onLoadUrl(url)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .to(AutoDispose.with(
                                        AndroidLifecycleScopeProvider.from(BrowserActivity.this)).forSingle())
                                .subscribe(html -> {
                                    view.loadDataWithBaseURL(url, html, "text/html", "utf-8", null);

                                }, throwable -> {
                                    showMessageIfInDebug(throwable.getMessage());
                                });

                        return true;
                    }
                });
            }
        }

        /*
          Get url from intent
         */
        final String url;
        if (intent.hasExtra(ARG_URL)) {
            url = intent.getStringExtra(ARG_URL);

        } else {
            if (intent.getData() == null) {
                // Url argument not found
                finish();
                return;
            }

            url = intent.getData().getQueryParameter("url");
        }
        // Save to intent
        intent.putExtra(ARG_URL, url);

        /*
          Load url
         */
        if (savedInstanceState == null) {
            mBinding.webView.loadUrl2(url);

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

    public void showMessageIfInDebug(@NonNull String message) {
        if (mColumn != null && mColumn.isDebug()) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    private class BrowserBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // If the column has not been assigned, return
            if (mColumn == null) return;

            final String action = intent.getAction();
            if (action == null) return;

            ArrayList<Column> columns;
            String columnId;
            int index;
            switch (action) {
                case Constants.ACTION_NOTIFY_COLUMN_UNINSTALLED:
                    columns = intent.getParcelableArrayListExtra(Constants.ARG_COLUMNS);
                    if (columns == null) return;

                    if (columns.contains(mColumn)) {
                        // If this column has been uninstalled, finish the browser page
                        finish();
                    }

                    break;

                case Constants.ACTION_NOTIFY_COLUMN_PREFERENCE_CHANGED:
                    columns = intent.getParcelableArrayListExtra(Constants.ARG_COLUMNS);
                    if (columns == null) return;

                    if (!columns.contains(mColumn)) {
                        // If this column is invisible, finish the browser page
                        finish();
                    }

                    break;

                case Constants.ACTION_NOTIFY_COLUMN_CONFIG_CHANGED:
                    columnId = intent.getStringExtra(Constants.ARG_COLUMNID);
                    if (columnId == null) return;

                    if (!mColumn.getId().toString().equalsIgnoreCase(columnId)) {
                        break;
                    }

                    // Refresh column object and refresh the page
                    mColumnManager.readConfig(UUID.fromString(columnId))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .to(AutoDispose.with(AndroidLifecycleScopeProvider.from(BrowserActivity.this)).forSingle())
                            .subscribe(column -> {
                                setupWebView(null, column);

                            }, throwable -> {
                                setupWebView(null, null);
                                showMessageIfInDebug(throwable.getMessage());
                            });

                    break;
            }
        }
    }
}
