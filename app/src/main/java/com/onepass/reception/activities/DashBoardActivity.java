package com.onepass.reception.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.onepass.reception.R;
import com.onepass.reception.adapters.PendingGuestAdapter;
import com.onepass.reception.databinding.ActivityDashBoardBinding;
import com.onepass.reception.dialog.LoadingDialog;
import com.onepass.reception.dialog.VerificationDialog;
import com.onepass.reception.insets.InsetsHelper;
import com.onepass.reception.models.response.ImageVerification;
import com.onepass.reception.models.response.PendingGuests;
import com.onepass.reception.repos.imageverificationrepo.ImageVerificationParams;
import com.onepass.reception.repos.imageverificationrepo.ImageVerificationRepo;
import com.onepass.reception.repos.pendingguestsrepo.PendingGuestParams;
import com.onepass.reception.repos.pendingguestsrepo.PendingGuestsRepo;
import com.onepass.reception.utils.AppUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DashBoardActivity extends AppCompatActivity {

    private ActivityDashBoardBinding binding;

    String filterBooking = null;

    List<PendingGuests> guests = new ArrayList<>();
    PendingGuestAdapter pendingGuestsAdapter;

    int selectedPosition = -1;

    private ActivityResultLauncher<Uri> takePictureLauncher;
    private Uri imageUri;
    private File imageFile;

    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        InsetsHelper.applySystemBarInsets(this,binding.getRoot(),true);


        setToolbarView();

        init();
    }

    private void setToolbarView() {
        binding.toolbar.setTitle(getString(R.string.capture_images));
        setSupportActionBar(binding.toolbar);
    }

    private void init(){
        setUpList();
        fetchPendingGuests();
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                isSuccess -> {
                    if (isSuccess) {
                        // imageUri contains the captured full-resolution image
                        AppUtils.showLog(imageUri.toString());
                        verifyImage();
                    }else{
                        AppUtils.showToast(DashBoardActivity.this,getString(R.string.error_capturing_image));
                    }
                }
        );

        binding.swipeRefresh.setOnRefreshListener(() -> {
            fetchPendingGuests();

            binding.swipeRefresh.setRefreshing(false);
        });

        loadingDialog = new LoadingDialog();
    }

    private void setUpList() {
        binding.rvPendingGuests.setLayoutManager(new LinearLayoutManager(this));
        pendingGuestsAdapter = new PendingGuestAdapter(DashBoardActivity.this,guests);
        binding.rvPendingGuests.setAdapter(pendingGuestsAdapter);
        DividerItemDecoration divider =
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);

        binding.rvPendingGuests.addItemDecoration(divider);
    }

    private void fetchPendingGuests() {

        preService();

        PendingGuestParams params = new PendingGuestParams();
        params.setBookingId(filterBooking);

        PendingGuestsRepo.getPendingRequests(
                params,
                pendingGuests -> {
                    guests.clear();
                    guests.addAll(pendingGuests);
                    runOnUiThread(()->{
                        postService();
                        updateList();
                    });
                },
                err->{
                    runOnUiThread(()->{
                        postService();
                        AppUtils.showToast(DashBoardActivity.this,getString(R.string.error_fetching_details));
                    });
                }
        );
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateList() {
        if(pendingGuestsAdapter!=null){
            pendingGuestsAdapter.notifyDataSetChanged();
        }
    }

    private void postService() {
        binding.rvPendingGuests.setVisibility(View.VISIBLE);
        binding.progress.setVisibility(View.GONE);
    }
    private void preService() {
        binding.rvPendingGuests.setVisibility(View.GONE);
        binding.progress.setVisibility(View.VISIBLE);
    }

    public void captureUserPicture(int position){
        selectedPosition = position;
        if(AppUtils.hasPermission(this, Manifest.permission.CAMERA)){
            openCamera();
        }else{
            AppUtils.askPermission(this, Manifest.permission.CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AppUtils.CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                AppUtils.showToast(DashBoardActivity.this,getString(R.string.please_grant_camera_permission_to_continue));
            }
        }
    }

    public void openCamera(){
        imageFile = new File(getExternalCacheDir(), guests.get(selectedPosition).getName()+"-"+System.currentTimeMillis() + ".jpg");

        imageUri = FileProvider.getUriForFile(
                this,
                getPackageName() + ".provider",
                imageFile
        );

        takePictureLauncher.launch(imageUri);
    }


    public void verifyImage(){

        if(imageFile!=null){

            loadingDialog.showLoader(DashBoardActivity.this);

            ImageVerificationParams params = new ImageVerificationParams();
            params.setBookingId(guests.get(selectedPosition).getBookingId());
            params.setCountryCode(guests.get(selectedPosition).getPhoneCountryCode());
            params.setPhoneNumber(guests.get(selectedPosition).getPhoneNumber());
            params.setSelfieImage(imageFile);

            ImageVerificationRepo.verifyImage(
                    params,
                    imageVerification->{
                        runOnUiThread(()->{
                            loadingDialog.dismissLoader();
                            VerificationDialog.showDialog(DashBoardActivity.this,imageVerification);
                            if(imageVerification.getFaceVerified()){
                                guests.remove(selectedPosition);
                                updateList();
                            }
                            imageClearing();
                        });

                    },
                    throwable -> {
                        loadingDialog.dismissLoader();
                        imageClearing();
                        AppUtils.showLog(throwable.getMessage());
                        runOnUiThread(()-> AppUtils.showToast(DashBoardActivity.this,getString(R.string.error_while_image_verification)));
                    }
            );

        }


    }


    public void imageClearing(){
        if(imageFile!=null){
            imageFile.delete();
        }
        imageFile = null;
        imageUri = null;
        selectedPosition = -1;
    }

    @Override
    protected void onDestroy() {
        AppUtils.deleteAllCachedImages(this);
        super.onDestroy();
    }

}
