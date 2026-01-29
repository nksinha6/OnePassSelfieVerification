package com.onepass.reception.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.onepass.reception.R;
import com.onepass.reception.databinding.ActivityDashBoardBinding;
import com.onepass.reception.databinding.ActivityQrscannerBinding;
import com.onepass.reception.dialog.infodialog.InfoDialog;
import com.onepass.reception.dialog.infodialog.InfoDialogParams;
import com.onepass.reception.insets.InsetsHelper;
import com.onepass.reception.utils.AppUtils;

public class QRScannerActivity extends AppCompatActivity {

    private ActivityQrscannerBinding binding;

    private DecoratedBarcodeView barcodeView;
    private boolean flashOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrscannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        InsetsHelper.applySystemBarInsets(this,binding.getRoot(),true);

        barcodeView = binding.barcodeScanner;

        if(AppUtils.hasPermission(this, Manifest.permission.CAMERA)){
            init();
        }else{
            AppUtils.askPermission(this, Manifest.permission.CAMERA,AppUtils.CAMERA_PERMISSION_CODE);
        }

    }

    public void init(){
        barcodeView.decodeContinuous(result -> {
            String text = result.getText();
            Intent intent = new Intent();
            intent.putExtra("SCAN_RESULT", text);
            setResult(RESULT_OK,intent);
            finish();
        });

        binding.ivClose.setOnClickListener(v->{
            getOnBackPressedDispatcher().onBackPressed();
        });
        binding.ivFlash.setOnClickListener(v -> toggleFlash());
    }

    private void toggleFlash() {
        if (flashOn) {
            binding.ivFlash.setImageResource(R.drawable.ic_flash_off);
            barcodeView.setTorchOff();
        } else {
            binding.ivFlash.setImageResource(R.drawable.ic_flash_on);
            barcodeView.setTorchOn();
        }
        flashOn = !flashOn;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == AppUtils.CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                init();
            }else {
                InfoDialogParams params = new InfoDialogParams(
                        this,
                        getString(R.string.okay),
                        getString(R.string.please_enable_camera_permission),
                        ()->AppUtils.askPermission(this, Manifest.permission.CAMERA,AppUtils.CAMERA_PERMISSION_CODE),
                        false,
                        null,
                        null
                );
                InfoDialog infoDialog = new InfoDialog(params);
                infoDialog.showDialog();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
    }
}