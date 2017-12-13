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

package cn.nekocode.hubs.ui.browser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.net.Uri;
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

import java.io.File;
import java.util.ArrayList;

import cn.nekocode.hubs.Constants;
import cn.nekocode.hubs.HubsApplication;
import cn.nekocode.hubs.R;
import cn.nekocode.hubs.base.BaseActivity;
import cn.nekocode.hubs.data.model.Hub;
import cn.nekocode.hubs.databinding.ActivityBrowserBinding;
import cn.nekocode.hubs.manager.base.BaseHubManager;
import cn.nekocode.hubs.manager.base.BaseFileManager;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class BrowserActivity extends BaseActivity {
    private static final String ARG_HUB = "hub";
    private static final String ARG_URL = "url";
    private static final String SAVED_WEBVIEW = "SAVED_WEBVIEW";
    private ActivityBrowserBinding mBinding;
    private BaseFileManager mFileManager;
    private BaseHubManager mHubManager;
    private final BroadcastReceiver mBroadcastReceiver = new BrowserBroadcastReceiver();
    private final HubsBridge mJsBridge = new HubsBridge(this);
    @Nullable
    private Hub mHub;
    private File mHubDir;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StateSaver.restoreInstanceState(this, savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_browser);
        mFileManager = HubsApplication.getDefaultFileManager(this);
        mHubManager = HubsApplication.getDefaultHubManager(this);

        /*
          Get arguments from intent
         */
        final Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        if (intent.hasExtra(ARG_HUB)) {
            mHub = intent.getParcelableExtra(ARG_HUB);
            setupWebView(savedInstanceState, mHub);

        } else {
            final String hubId;
            if (intent.getData() != null && !TextUtils.isEmpty(
                    hubId = intent.getData().getQueryParameter("hub_id"))) {

                mHubManager.readConfig(hubId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .to(AutoDispose.with(AndroidLifecycleScopeProvider.from(this)).forSingle())
                        .subscribe(hub -> {
                            setupWebView(savedInstanceState, hub);

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
        intentFilter.addAction(Constants.ACTION_NOTIFY_HUB_INSTALLED);
        intentFilter.addAction(Constants.ACTION_NOTIFY_HUB_UNINSTALLED);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mBroadcastReceiver, intentFilter);
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private void setupWebView(Bundle savedInstanceState, Hub hub) {
        // Add Js Interface
        mBinding.webView.addJavascriptInterface(mJsBridge, HubsBridge.JS_NAME);

        // Save to intent
        final Intent intent = getIntent();
        intent.putExtra(ARG_HUB, hub);
        mHub = hub;

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
            if (hub == null || TextUtils.isEmpty(hub.getBrowser())) {
                mBinding.webView.loadUrl2(url);

            } else {
                mHubDir = mFileManager.getHubDirectory(hub.getId());
                final String browserUrl = Uri.fromFile(new File(mHubDir, hub.getBrowser()))
                        .buildUpon()
                        .appendQueryParameter("url", url)
                        .build()
                        .toString();
                mBinding.webView.loadUrl2(browserUrl);

                mBinding.webView.setCallback(new WebViewCallback() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        return false;
                    }
                });
            }

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
        if (mHub != null && mHub.isDebug()) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    private class BrowserBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // If the hub has not been assigned, return
            if (mHub == null) return;

            final String action = intent.getAction();
            if (action == null) return;

            ArrayList<Hub> hubs;
            Hub hub;
            String hubId;
            switch (action) {
                case Constants.ACTION_NOTIFY_HUB_INSTALLED:
                    hub = intent.getParcelableExtra(Constants.ARG_HUB);
                    hubId = intent.getStringExtra(Constants.ARG_HUB_ID);
                    if (hub == null && hubId == null) break;
                    if (hubId == null) hubId = hub.getId();

                    if (mHub == null || !hubId.equalsIgnoreCase(mHub.getId())) {
                        break;
                    }

                    // Refresh hub object and refresh the page
                    (hub != null ? Single.just(hub) : mHubManager.readConfig(hubId))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .to(AutoDispose.with(AndroidLifecycleScopeProvider.from(BrowserActivity.this)).forSingle())
                            .subscribe(_hub -> {
                                setupWebView(null, _hub);

                            }, throwable -> {
                                setupWebView(null, null);
                                showMessageIfInDebug(throwable.getMessage());
                            });

                    break;

                case Constants.ACTION_NOTIFY_HUB_UNINSTALLED:
                    hubs = intent.getParcelableArrayListExtra(Constants.ARG_HUBS);
                    if (hubs == null) return;

                    if (hubs.contains(mHub)) {
                        // If this hub has been uninstalled, finish the browser page
                        finish();
                    }
                    break;
            }
        }
    }
}
