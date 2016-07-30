package com.hexonxons.hive.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;

public class SpacesItemDecorator extends RecyclerView.ItemDecoration {
    // Spacing dp.
    private static final float GRID_SPACING = 6.0f;
    // Spacing px.
    private final int mSpace;

    public SpacesItemDecorator(@NonNull Context context) {
        mSpace = (int) convertDpToPixel(GRID_SPACING, context);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
            RecyclerView.State state) {
        outRect.left = mSpace;
        outRect.right = mSpace;
        outRect.bottom = mSpace;
        outRect.top = mSpace;
    }

    private static float convertDpToPixel(float dp, @NonNull Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160.0f);
    }
}
