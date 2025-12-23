package com.onepass.reception.insets;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class InsetsHelper {
    public static void applySystemBarInsets(Context context, View rootView, boolean hasToolBar) {


//        Window window = ((Activity) context).getWindow();
//
//        // Enable edge-to-edge
//        WindowCompat.setDecorFitsSystemWindows(window, false);
//
//        // Explicitly set your desired status bar color
//        window.setStatusBarColor(ContextCompat.getColor(context, R.color.colorPrimary));
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//
//        // Set dark/light icons depending on background color
//        WindowInsetsControllerCompat insetsController =
//                WindowCompat.getInsetsController(window, window.getDecorView());
//        if (insetsController != null) {
//            insetsController.setAppearanceLightStatusBars(false); // false if dark background
//        }

           ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
               Insets sysInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                  if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                      ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                      mlp.leftMargin = sysInsets.left;
                      mlp.rightMargin = sysInsets.right;
                      mlp.bottomMargin = sysInsets.bottom;
                      mlp.topMargin = sysInsets.top;
                      v.setLayoutParams(mlp);
                  }

               return WindowInsetsCompat.CONSUMED;
           });
    }
}
