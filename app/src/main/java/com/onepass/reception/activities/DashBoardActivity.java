package com.onepass.reception.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.onepass.reception.R;
import com.onepass.reception.adapters.PendingGuestAdapter;
import com.onepass.reception.databinding.ActivityDashBoardBinding;
import com.onepass.reception.dialog.LoadingDialog;
import com.onepass.reception.dialog.VerificationDialog;
import com.onepass.reception.dialog.infodialog.InfoDialog;
import com.onepass.reception.dialog.infodialog.InfoDialogParams;
import com.onepass.reception.insets.InsetsHelper;
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


    private Uri imageUri;
    private File imageFile;

    LoadingDialog loadingDialog;

    private ActivityResultLauncher<Intent> qrLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        InsetsHelper.applySystemBarInsets(this,binding.getRoot(),true);


        setToolbarView();

        qrLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                                String contents = result.getData().getStringExtra("SCAN_RESULT");
                                AppUtils.showLog("Scanned: " + contents);
                                verifyLocation();
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
        binding.toolbar.setTitle(getString(R.string.guest_list));
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
    }

    private void fetchPendingGuests() {

        preService();

        PendingGuestParams params = new PendingGuestParams();
        params.setBookingId(filterBooking);

        PendingGuestsRepo.getPendingRequests(
                params,
                pendingGuests -> {
                    guests.clear();
                    if(pendingGuests.getPendingGuests()!=null && !pendingGuests.getPendingGuests().isEmpty()){
                        guests.addAll(pendingGuests.getPendingGuests());
                        runOnUiThread(()->{
                            postService();
                            updateList();
                        });
                    }else{
                        runOnUiThread(()->{
                            showErrorDialog(getString(R.string.no_pending_guests_found));
                        });
                    }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == AppUtils.LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                init();
            }else {
                InfoDialogParams params = new InfoDialogParams(
                        this,
                        getString(R.string.okay),
                        getString(R.string.please_enable_location_permission),
                        ()->AppUtils.askPermission(this, Manifest.permission.ACCESS_FINE_LOCATION,AppUtils.LOCATION_PERMISSION_CODE),
                        false,
                        null,
                        null
                );
                InfoDialog infoDialog = new InfoDialog(params);
                infoDialog.showDialog();
            }
        }

    }


    public void openQRScanner(int position){
        selectedPosition = position;
        Intent intent = new Intent(this, QRScannerActivity.class);
        qrLauncher.launch(intent);
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
                        });

                    },
                    throwable -> {
                        AppUtils.showLog(throwable.getMessage());
                        runOnUiThread(()-> {
                            dismissLoader();
                            showErrorDialog(throwable.getMessage());
                        });
                    }
            );

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
