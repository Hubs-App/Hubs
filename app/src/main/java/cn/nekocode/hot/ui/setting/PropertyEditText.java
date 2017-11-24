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

package cn.nekocode.hot.ui.setting;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class PropertyEditText extends android.support.v7.widget.AppCompatEditText {
    private PropertyVO mPropertyVO;


    public PropertyEditText(Context context) {
        super(context);
        setup();
    }

    public PropertyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public PropertyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    private void setup() {
        addTextChangedListener(mWatch);
    }

    public void setVO(@NonNull PropertyVO propertyVO) {
        // Set mPropertyVO to null before setText(), because setText will trigger onSelectionChanged()
        this.mPropertyVO = null;
        if (propertyVO.getValue() != null) {
            setText(propertyVO.getValue());
        }
        this.mPropertyVO = propertyVO;

        // Set selection position
        setSelection(propertyVO.getSelectionStart(), propertyVO.getSelectionEnd());

        // Check if need to get foucus
        if (propertyVO.isFoucused()) {
            requestFocusFromTouch();
        }
    }

    public void resetText(CharSequence text) {
        setText(text);

        if (mPropertyVO != null) {
            mPropertyVO.setOldValue(text.toString());
        }
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        if (mPropertyVO != null) {
            mPropertyVO.setFoucused(focused);
        }

        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        if (mPropertyVO != null) {
            mPropertyVO.setSelectionStart(selStart);
            mPropertyVO.setSelectionEnd(selEnd);
        }

        super.onSelectionChanged(selStart, selEnd);
    }

    private TextWatcher mWatch = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mPropertyVO != null) {
                mPropertyVO.setValue(s.toString());
            }
        }
    };
}
