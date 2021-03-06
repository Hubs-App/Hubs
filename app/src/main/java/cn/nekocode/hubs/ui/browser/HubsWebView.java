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

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class HubsWebView extends WebView {
    private HubsWebViewClient mWebViewClient;
    private WebViewCallback mCallback;


    public HubsWebView(Context context) {
        super(context);
        init();
    }

    public HubsWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HubsWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public HubsWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        final WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        mWebViewClient = new HubsWebViewClient();
        setWebViewClient(mWebViewClient);
    }

    public void setCallback(WebViewCallback callback) {
        this.mCallback = callback;
        mWebViewClient.setCallback(callback);
    }

    /**
     * This method will call shouldOverrideUrlLoading() before loadUrl()
     */
    public void loadUrl2(String url) {
        if (mWebViewClient.shouldOverrideUrlLoading(this, url)) {
            return;
        }

        loadUrl(url);
    }
}
