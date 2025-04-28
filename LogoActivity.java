package com.sruthi.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class LogoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        // Delay for splash effect (5 seconds)
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(LogoActivity.this, LoginActivity.class); // ✅ Navigate to LoginActivity
            startActivity(intent);
            finish();
        }, 2000); // Changed delay to 5000 milliseconds (5 seconds)
    }
}
