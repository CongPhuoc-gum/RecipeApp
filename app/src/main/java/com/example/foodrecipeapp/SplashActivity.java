package com.example.foodrecipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.sax.StartElementListener;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.foodrecipeapp.utils.FirebaseUtil;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);


        new Handler().postDelayed(() -> {
            // Kiểm tra trạng thái đăng nhập
            if (FirebaseUtil.isLoggedIn()) {
                navigateToDashboard();
            } else {
                // Nếu chưa đăng nhập, chuyển đến LoginActivity
                navigateToLogin();
            }
        }, 2000);
    }

    private void navigateToLogin() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }
    private void navigateToDashboard() {
        startActivity(new Intent(SplashActivity.this, Dashboard.class));
        finish();
    }
}