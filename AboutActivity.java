package com.sruthi.myapp;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Initialize back button
        backArrow = findViewById(R.id.ic_back_arrow);

        // Handle back button click
        backArrow.setOnClickListener(view -> finish()); // Close AboutActivity
    }
}
