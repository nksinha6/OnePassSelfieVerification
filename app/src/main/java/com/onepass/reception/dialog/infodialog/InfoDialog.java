package com.onepass.reception.dialog.infodialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.onepass.reception.R;

public class InfoDialog {

    InfoDialogParams params;

    public InfoDialog(InfoDialogParams params){
        this.params = params;
    }


    public void showDialog(){
        Dialog dialog = new Dialog(params.getContext());
        dialog.setContentView(R.layout.dialog_info);
        dialog.setCancelable(params.isCancelable());
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (!((Activity) params.getContext()).isFinishing()) {
            dialog.show();
        }

        ((TextView) dialog.findViewById(R.id.tv_desc)).setText(params.getMessage());

        MaterialButton closeBtn = dialog.findViewById(R.id.btn_close);
        closeBtn.setText(params.getButtonText());

        dialog.findViewById(R.id.btn_close).setOnClickListener((View v) -> {
            params.getCallback().onButtonClick();
            dialog.dismiss();
        });

       try{
           if(params.getOnDialogCancelled()!=null) {
               dialog.setOnCancelListener(params.getOnDialogCancelled());
           }
           if(params.getOnDismissListener()!=null) {
               dialog.setOnDismissListener(params.getOnDismissListener());
           }
       }catch (Exception e){
       }

    }
}
