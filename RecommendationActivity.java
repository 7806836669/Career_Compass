package com.sruthi.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RecommendationActivity extends AppCompatActivity {

    private TextView recommendationTitle, careerText, collegeText;
    private Button btnBackToHome, btnMoreDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);

        recommendationTitle = findViewById(R.id.recommendationTitle);
        careerText = findViewById(R.id.careerText);
        collegeText = findViewById(R.id.collegeText);
        btnBackToHome = findViewById(R.id.btnBackToHome);
        btnMoreDetails = findViewById(R.id.btnMoreDetails);

        // Get recommended field and college from Intent
        String recommendedField = getIntent().getStringExtra("field_name");
        String recommendedCollege = getIntent().getStringExtra("message");

        // Display recommendations
        recommendationTitle.setText("🎯 Your Personalized Recommendations");
        careerText.setText("Recommended Field: " + recommendedField);
        collegeText.setText("Suggestion: " + recommendedCollege);

        // Button to go back to home
        btnBackToHome.setOnClickListener(view -> {
            Intent intent = new Intent(RecommendationActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        // Button to see more details about career & colleges
        btnMoreDetails.setOnClickListener(view -> {
            Intent intent = new Intent(RecommendationActivity.this,ChatbotActivity.class);
            startActivity(intent);
        });
    }
}
