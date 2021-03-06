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

import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import cn.nekocode.hubs.BuildConfig;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class HubsWebViewClient extends WebViewClient {
    private static final String SCHEME_APP = BuildConfig.SCHEME;
    private static final String SCHEME_HTTP = "http";
    private static final String SCHEME_HTTPS = "https";
    private WebViewCallback mCallback;


    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        final Uri uri = Uri.parse(url);
        final String scheme = uri.getScheme();

        if (SCHEME_HTTP.equalsIgnoreCase(scheme) || SCHEME_HTTPS.equalsIgnoreCase(scheme)) {
            return mCallback != null ?
                    mCallback.shouldOverrideUrlLoading(view, url) : super.shouldOverrideUrlLoading(view, url);

        } else if (SCHEME_APP.equalsIgnoreCase(scheme)) {
            try {
                final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                view.getContext().startActivity(intent);
                return true;

            } catch (Exception e) {
                return mCallback != null ?
                        mCallback.shouldOverrideUrlLoading(view, url) : super.shouldOverrideUrlLoading(view, url);
            }

        } else {
            return mCallback != null ?
                    mCallback.shouldOverrideUrlLoading(view, url) : super.shouldOverrideUrlLoading(view, url);
        }
    }

    public void setCallback(WebViewCallback callback) {
        this.mCallback = callback;
    }
}
