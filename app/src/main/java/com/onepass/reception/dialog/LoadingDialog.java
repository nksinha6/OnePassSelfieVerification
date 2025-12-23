package com.onepass.reception.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup;

import com.onepass.reception.R;

public class LoadingDialog {

    public Dialog loading = null;
    public void showLoader(Context context) {

        if (loading == null) {
            loading = new Dialog(context);
            loading.setContentView(R.layout.dialog_loader);
            loading.setCancelable(false);
            loading.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            loading.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        if (!((Activity) context).isFinishing()) {
            loading.show();
        }
    }


    public void dismissLoader() {
        if (loading != null && loading.isShowing()) {
            loading.dismiss();
        }
    }
}
