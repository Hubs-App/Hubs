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

package cn.nekocode.hot.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import cn.nekocode.hot.R;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

    @IntDef({HORIZONTAL_LIST, VERTICAL_LIST})
    public @interface Orientation {
    }

    private Drawable mDivider;
    private
    @Orientation
    int mOrientation;
    private int mMarginStart = 0;
    private int mMarginEnd = 0;
    private int mDividerWidth;
    private boolean mDrawLast = true;


    public static RecyclerView.ItemDecoration obtainDefault(@NonNull Context context) {
        final Resources resources = context.getResources();
        return new DividerItemDecoration(
                VERTICAL_LIST,
                ContextCompat.getColor(context, R.color.divider),
                2,
                resources.getDimensionPixelSize(R.dimen.defaultMargin),
                resources.getDimensionPixelSize(R.dimen.defaultMargin),
                false
        );
    }

    public DividerItemDecoration(@Orientation int orientation, int color) {
        this(orientation, color, 2);
    }

    public DividerItemDecoration(@Orientation int orientation, int color, int dividerWidth) {
        this(orientation, color, dividerWidth, 0, 0, true);
    }

    public DividerItemDecoration(@Orientation int orientation, int color, int dividerWidth, int marginStart, int marginEnd, boolean drawLast) {
        mDivider = new ColorDrawable(color);
        mOrientation = orientation;
        mDividerWidth = dividerWidth;
        mMarginStart = marginStart;
        mMarginEnd = marginEnd;
        mDrawLast = drawLast;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == VERTICAL_LIST) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    private void drawVertical(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int count = parent.getChildCount() - (mDrawLast ? 0 : 1);
        for (int i = 0; i < count; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDividerWidth;
            mDivider.setBounds(left + mMarginStart, top, right - mMarginEnd, bottom);
            mDivider.draw(c);
        }
    }

    private void drawHorizontal(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();

        final int count = parent.getChildCount() - (mDrawLast ? 0 : 1);
        for (int i = 0; i < count; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDividerWidth;
            mDivider.setBounds(left, top + mMarginStart, right, bottom - mMarginEnd);
            mDivider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == VERTICAL_LIST) {
            outRect.set(0, 0, 0, mDividerWidth);
        } else {
            outRect.set(0, 0, mDividerWidth, 0);
        }
    }
}
