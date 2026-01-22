package com.onepass.reception.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.onepass.reception.R;
import com.onepass.reception.databinding.ActivityMainBinding;
import com.onepass.reception.insets.InsetsHelper;
import com.onepass.reception.models.response.LoginResponse;
import com.onepass.reception.network.ApiClient;
import com.onepass.reception.network.ApiService;
import com.onepass.reception.repos.loginrepo.LoginParams;
import com.onepass.reception.repos.loginrepo.LoginRepo;
import com.onepass.reception.utils.AppUtils;
import com.onepass.reception.utils.SessionManager;

import javax.xml.validation.Validator;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    boolean showPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        EdgeToEdge.enable(this);
        InsetsHelper.applySystemBarInsets(this,binding.getRoot(),true);

        validateIsLoggedInUser();

        init();

    }

    private void init(){
        handlePasswordIconClick();
        binding.btnSubmit.setOnClickListener(v->{
            if(validateInputs()){
                loginUser();
            }
        });
    }

    private void loginUser() {

        preService();

        String userId = binding.etUserId.getText().toString();
        String password = binding.etPassword.getText().toString();

        LoginParams params = new LoginParams(userId,password,1);

        LoginRepo.login(
                params,
                response -> {
                    SessionManager.reset();
                    saveData(response);
                    runOnUiThread(()->{
                        postService();
                        navigateToHome();
                    });
                }, err->{
                    runOnUiThread(()->{
                        postService();
                        AppUtils.showToast(MainActivity.this,getString(R.string.invalid_credentials));
                    });
                }
        );
    }

    private void navigateToHome() {
        Intent intent = new Intent(MainActivity.this, DashBoardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void saveData(LoginResponse response) {
        SharedPreferences shPref = AppUtils.getSharedPreferences(MainActivity.this);
        SharedPreferences.Editor editor = shPref.edit();
        editor.putString("access_token",response.getAccessToken());
        editor.putString("refresh_token",response.getRefreshToken());
        editor.putString("token_expires_at",response.getExpiresAt());
        editor.putBoolean("is_logged_in",true);
        editor.commit();
    }

    private boolean validateInputs() {
        String userId = binding.etUserId.getText().toString();
        if(userId.isEmpty() || !AppUtils.isValidEmail(userId)){
            AppUtils.showToast(MainActivity.this,getString(R.string.please_enter_a_valid_user_id));
            return false;
        }

        String password = binding.etPassword.getText().toString();
        if(password.isEmpty()){
            AppUtils.showToast(MainActivity.this,getString(R.string.please_enter_a_valid_password));
            return false;
        }

        return true;

    }

    @SuppressLint("ClickableViewAccessibility")
    private void handlePasswordIconClick() {

        binding.etPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {

                // Right drawable index = 2
                Drawable drawableEnd = binding.etPassword.getCompoundDrawables()[2];

                if (drawableEnd != null) {
                    int drawableWidth = drawableEnd.getBounds().width();

                    // Check if touch is within drawable area
                    if (event.getRawX() >= (binding.etPassword.getRight() - drawableWidth - binding.etPassword.getPaddingEnd())) {

                        togglePasswordVisibility(binding.etPassword);
                        return true;
                    }
                }
            }
            return false;
        });

    }

    private void togglePasswordVisibility(EditText editText) {
        if (showPassword) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editText.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.ic_show_password, 0);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            editText.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.ic_hide_password, 0);
        }

        showPassword = !showPassword;

        // Move cursor to end
        editText.setSelection(editText.getText().length());
    }

    private void validateIsLoggedInUser() {
        SharedPreferences shPref = AppUtils.getSharedPreferences(MainActivity.this);
        if(shPref.contains("is_logged_in")){
            navigateToHome();
        }
    }

    private void preService(){
        binding.btnSubmit.setVisibility(View.GONE);
        binding.progress.setVisibility(View.VISIBLE);
    }

    private void postService(){
        binding.btnSubmit.setVisibility(View.VISIBLE);
        binding.progress.setVisibility(View.GONE);
    }
}
