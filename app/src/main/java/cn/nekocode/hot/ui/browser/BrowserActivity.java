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

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;

import com.evernote.android.state.StateSaver;

import cn.nekocode.hot.R;
import cn.nekocode.hot.base.BaseActivity;
import cn.nekocode.hot.databinding.ActivityBrowserBinding;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class BrowserActivity extends BaseActivity {
    private static final String SAVED_WEBVIEW = "SAVED_WEBVIEW";
    private ActivityBrowserBinding mBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StateSaver.restoreInstanceState(this, savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_browser);

        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getData() != null) {
                final Uri uri = getIntent().getData();
                mBinding.webView.loadUrl(uri.getQueryParameter("url"));
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
}
