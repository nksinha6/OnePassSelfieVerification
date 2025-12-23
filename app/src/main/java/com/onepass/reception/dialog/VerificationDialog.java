package com.onepass.reception.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.onepass.reception.R;
import com.onepass.reception.models.response.ImageVerification;

public class VerificationDialog {



    public static void showDialog(Context context, ImageVerification verification){

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_verification);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView tv = dialog.findViewById(R.id.tv_message);
        tv.setText(verification.getFaceVerified()? context.getString(R.string.verification_successful) : context.getString(R.string.verification_failed));

        LottieAnimationView animationView = dialog.findViewById(R.id.lottie_view);
        animationView.setAnimation(verification.getFaceVerified()?"anim_success.json":"anim_failure.json");

        dialog.findViewById(R.id.btn_close).setOnClickListener(v->{
            dialog.dismiss();
        });

        dialog.show();
    }
}
