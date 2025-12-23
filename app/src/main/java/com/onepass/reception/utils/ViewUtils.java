package com.onepass.reception.utils;

import android.content.res.Resources;

public final class ViewUtils {

    public static int dpToPx(int dp) {
        return Math.round(
                dp * Resources.getSystem().getDisplayMetrics().density
        );
    }
}

