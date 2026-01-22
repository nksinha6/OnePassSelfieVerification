package com.onepass.reception.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.onepass.reception.R;
import com.onepass.reception.activities.DashBoardActivity;
import com.onepass.reception.activities.MainActivity;
import com.onepass.reception.dialog.infodialog.InfoDialog;
import com.onepass.reception.dialog.infodialog.InfoDialogParams;
import com.onepass.reception.models.domain.Booking;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class AppUtils {

    public static final int CAMERA_PERMISSION_CODE = 101;
    public static final int LOCATION_PERMISSION_CODE = 102;
    public static final String images="images";

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("\\d{10}");
    }

    public static boolean isValidEmail(String email){
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static void showToast(Context context,String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences(context.getString(R.string.app_shared_preferences),Context.MODE_PRIVATE);
    }


    @SuppressLint("DefaultLocale")
    public static List<Booking> getDummyBookings(){
        List<Booking> bookings = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {

            Booking booking = new Booking();

            booking.setId((long) i);
            booking.setBookingId("BOOK-" + i);
            booking.setUserName("User " + i);

            booking.setGuests(Arrays.asList("Guest A" + i, "Guest B" + i, "Guest C" + i, "Guest D" + i, "Guest E" + i));

            booking.setCheckInDate("2025-01-" + String.format("%02d", i));
            booking.setCheckOutDate("2025-01-" + String.format("%02d", i + 1));
            booking.setHotelName("The Grand Hotel");
            bookings.add(booking);
        }

        return bookings;
    }

    public static boolean hasPermission(Context context, String permission){
        return ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void askPermission(Activity context, String permission, int permissionCode){
        ActivityCompat.requestPermissions(
                context,
                new String[]{permission},
                permissionCode
        );
    }

    public static File createImageFile(Context context, String fileName) throws IOException {

        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir.getAbsolutePath()+"/"+fileName+".jpg");
        return image;
    }
    
    public static void openCamera(Context context, String name, ActivityResultLauncher<Uri> cameraResultIntentLauncher) {
        try {
            File photoFile = createImageFile(context,name+"-"+System.currentTimeMillis());
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri uri = FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".provider",
                    photoFile
            );
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            cameraResultIntentLauncher.launch(uri);
        } catch (IOException e) {
            showToast(context,context.getString(R.string.error_while_creating_image));
        }
    }

    public static void deleteAllCachedImages(Context context) {
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir == null) return;

        File[] files = cacheDir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isFile() &&
                    (file.getName().endsWith(".jpg") ||
                            file.getName().endsWith(".jpeg") ||
                            file.getName().endsWith(".png") ||
                            file.getName().endsWith(".webp"))) {

                file.delete();
            }
        }
    }

    public static void showLog(String message) {
        Log.v("TAG",message);
    }

    public static RequestBody getRequestBody(JSONObject postObject) {

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        return RequestBody.create(JSON, String.valueOf(postObject));

    }

    public static Map<String, String> getHeaders() {
        Map<String,String> headers = new HashMap<>();
        headers.put("Authorization","Bearer "+TokenProvider.getInstance().getAccessToken());
        return headers;
    }

    public static boolean isGpsEnabled(Context context) {

        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        return gps_enabled;
    }

    public static void showGpsDialog(Context context) {

        InfoDialogParams params = new InfoDialogParams(
                context,
                context.getString(R.string.location_turn_on),
                context.getString(R.string.okay),
                ()->{
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(intent);
                },
                false,
                null,
                null

        );

        InfoDialog infoDialog = new InfoDialog(params);
        infoDialog.showDialog();
    }

    public static void getLocation(Context context, LocationCallback locationCallback){
        FusedLocationProviderClient fusedClient = LocationServices.getFusedLocationProviderClient(context);
        CancellationTokenSource tokenSource = new CancellationTokenSource();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationCallback.onError(context.getString(R.string.please_enable_location_permission));
            return;
        }
        fusedClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                tokenSource.getToken()
        ).addOnSuccessListener(location -> {

            // Cancel immediately on success
            tokenSource.cancel();

            if (location != null) {
                locationCallback.onLocationFetched(location.getLatitude(),location.getLongitude());
            } else {
                fusedClient.getLastLocation()
                        .addOnSuccessListener(lastLocation -> {
                            if (lastLocation != null) {
                                locationCallback.onLocationFetched(lastLocation.getLatitude(),lastLocation.getLongitude());
                            } else {
                                locationCallback.onError(context.getString(R.string.no_location_available));
                                showLog("No location available");
                            }
                        });
            }
        }).addOnFailureListener(e -> {
            tokenSource.cancel();
            locationCallback.onError(e.getMessage());
            showLog("No location available "+e.getMessage());
        });
    }

    public static void logout(Context context) {
        SharedPreferences shPref = getSharedPreferences(context);
        shPref.edit().clear().commit();
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static void sessionExpired(Context appContext) {
        InfoDialogParams params = new InfoDialogParams(
                appContext,
                appContext.getString(R.string.logout),
                appContext.getString(R.string.your_session_has_expired_please_login_again),
                ()->logout(appContext),
                false,
                null,
                null
        );

        InfoDialog infoDialog = new InfoDialog(params);
        infoDialog.showDialog();

    }
}
