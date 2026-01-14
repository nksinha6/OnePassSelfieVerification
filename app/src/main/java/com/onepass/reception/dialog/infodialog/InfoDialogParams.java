package com.onepass.reception.dialog.infodialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;




public class InfoDialogParams {
    public InfoDialogParams(Context context, String buttonText, String message, InfoDialogCallback callback, boolean cancelable, Dialog.OnCancelListener onDialogCancelled, DialogInterface.OnDismissListener onDismissListener) {
        this.context = context;
        this.buttonText = buttonText;
        this.message = message;
        this.callback = callback;
        this.cancelable = cancelable;
        this.onDialogCancelled = onDialogCancelled;
        this.onDismissListener = onDismissListener;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public InfoDialogCallback getCallback() {
        return callback;
    }

    public void setCallback(InfoDialogCallback callback) {
        this.callback = callback;
    }

    public boolean isCancelable() {
        return cancelable;
    }

    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }

    public Dialog.OnCancelListener getOnDialogCancelled() {
        return onDialogCancelled;
    }

    public void setOnDialogCancelled(Dialog.OnCancelListener onDialogCancelled) {
        this.onDialogCancelled = onDialogCancelled;
    }

    public DialogInterface.OnDismissListener getOnDismissListener() {
        return onDismissListener;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }


    private Context context;
    private String buttonText;
    private String message;
    private InfoDialogCallback callback;
    private boolean cancelable;
    private Dialog.OnCancelListener onDialogCancelled;
    private  DialogInterface.OnDismissListener onDismissListener;
}
