package com.onepass.reception.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.onepass.reception.R;
import com.onepass.reception.adapters.PendingGuestAdapter;
import com.onepass.reception.databinding.ActivityDashBoardBinding;
import com.onepass.reception.dialog.LoadingDialog;
import com.onepass.reception.dialog.VerificationDialog;
import com.onepass.reception.dialog.infodialog.InfoDialog;
import com.onepass.reception.dialog.infodialog.InfoDialogParams;
import com.onepass.reception.insets.InsetsHelper;
import com.onepass.reception.models.response.ImageVerification;
import com.onepass.reception.models.response.PendingGuests;
import com.onepass.reception.repos.imageverificationrepo.ImageVerificationParams;
import com.onepass.reception.repos.imageverificationrepo.ImageVerificationRepo;
import com.onepass.reception.repos.pendingguestsrepo.PendingGuestParams;
import com.onepass.reception.repos.pendingguestsrepo.PendingGuestsRepo;
import com.onepass.reception.utils.AppUtils;
import com.onepass.reception.utils.BaseActivity;
import com.onepass.reception.utils.LocationCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DashBoardActivity extends BaseActivity {

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

        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                isSuccess -> {
                    if (isSuccess) {
                        // imageUri contains the captured full-resolution image
                        AppUtils.showLog(imageUri.toString());
                        verifyLocation();
                    }else{
                        AppUtils.showToast(DashBoardActivity.this,getString(R.string.error_capturing_image));
                    }
                }
        );

        if(AppUtils.hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            init();
        }else{
            AppUtils.askPermission(this, Manifest.permission.ACCESS_FINE_LOCATION,AppUtils.LOCATION_PERMISSION_CODE);
        }


    }

    private void setToolbarView() {
        binding.toolbar.setTitle(getString(R.string.capture_images));
        setSupportActionBar(binding.toolbar);
    }

    private void init(){
        setUpList();
        fetchPendingGuests();


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
                       showErrorDialog(err.getMessage());
                    });
                }
        );
    }

    private void showErrorDialog(String message) {
        InfoDialogParams pms = new InfoDialogParams(
                DashBoardActivity.this,
                getString(R.string.close),
                message,
                null,
                true,
                null,
                null

        );
        InfoDialog infoDialog = new InfoDialog(pms);
        infoDialog.showDialog();
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
            AppUtils.askPermission(this, Manifest.permission.CAMERA,AppUtils.CAMERA_PERMISSION_CODE);
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
        if (requestCode == AppUtils.LOCATION_PERMISSION_CODE) {
            init();
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


    public void verifyLocation(){
        showLoader();
        AppUtils.getLocation(this, new LocationCallback() {
            @Override
            public void onLocationFetched(double latitude, double longitude) {
                AppUtils.showLog("Location: "+latitude+", "+longitude);
                verifyImage(latitude,longitude);
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(()->AppUtils.showToast(DashBoardActivity.this,errorMessage));
                dismissLoader();
            }
        });
    }

    public void verifyImage(double latitude, double longitude){

        if(imageFile!=null){
            ImageVerificationParams params = new ImageVerificationParams();
            params.setId(guests.get(selectedPosition).getId());
            params.setBookingId(guests.get(selectedPosition).getBookingId());
            params.setCountryCode(guests.get(selectedPosition).getPhoneCountryCode());
            params.setPhoneNumber(guests.get(selectedPosition).getPhoneNumber());
            params.setSelfieImage(imageFile);
            params.setLatitude(latitude);
            params.setLongitude(longitude);

            ImageVerificationRepo.verifyImage(
                    params,
                    imageVerification->{
                        runOnUiThread(()->{
                            dismissLoader();
                            VerificationDialog.showDialog(DashBoardActivity.this,imageVerification);

                            if(!imageVerification.getResult().getFaceMatchResult().equalsIgnoreCase("NO")){
                                guests.remove(selectedPosition);
                                updateList();
                            }

                            imageClearing();
                        });

                    },
                    throwable -> {
                        AppUtils.showLog(throwable.getMessage());
                        runOnUiThread(()-> {
                            dismissLoader();
                            showErrorDialog(throwable.getMessage());
                        });
                        imageClearing();
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


    public void showLoader(){
        loadingDialog.showLoader(DashBoardActivity.this);
    }
    public void dismissLoader(){
        loadingDialog.dismissLoader();
    }

    @Override
    protected void onDestroy() {
        AppUtils.deleteAllCachedImages(this);
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);
        setupLogout(menu.findItem(R.id.menu_logout));

        return super.onCreateOptionsMenu(menu);
    }


    void setupLogout(MenuItem item){
        item.getActionView().findViewById(R.id.btn_logout).setOnClickListener(v->{
            AppUtils.logout(DashBoardActivity.this);
        });
    }


}
