package com.sruthi.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ResultActivity extends AppCompatActivity {

    private TextView bravoText, resultText, collegeText;
    private Button btnConfused, btnBooksGuides;

    private String field, college, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        bravoText = findViewById(R.id.bravoText);
        resultText = findViewById(R.id.resultText);
        collegeText = findViewById(R.id.collegeText);
        btnConfused = findViewById(R.id.btnConfused);
        btnBooksGuides = findViewById(R.id.btnBooksGuides);

        // Assuming user ID or some identifier to fetch career results
        String userId = "some_user_id"; // Replace with actual user ID

        // Send request to the backend to get career recommendations
//        getCareerRecommendations(userId);

        Intent getIntent = getIntent();
        if(getIntent!=null) {
            field = getIntent.getStringExtra("recommended_field");
            college = getIntent.getStringExtra("recommended_college");
            name = getIntent.getStringExtra("user_name");
            resultText.setText("Your Recommended Career: " + field);
            collegeText.setText("Recommended College: " + college);
        } else {
            Toast.makeText(this, "Cannot Get College", Toast.LENGTH_SHORT).show();
        }

        // Button listeners for confusion or book guides
        btnConfused.setOnClickListener(view -> {
            Intent intent = new Intent(ResultActivity.this, FieldQuestionsActivity.class);
            intent.putExtra("user_name", name);
            intent.putExtra("selected_field", field);
            startActivity(intent);
        });

        btnBooksGuides.setOnClickListener(view -> {
            Intent intent = new Intent(ResultActivity.this, BooksGuidesActivity.class);
            startActivity(intent);
        });
    }

    private void getCareerRecommendations(String userId) {
        String url = Ipv4Connection.BASE_URL+"store_answers.php"; // Backend URL for career recommendation

        // Prepare request JSON
        JSONObject requestParams = new JSONObject();
        try {
            requestParams.put("user_name", userId);
            requestParams.put("answers", new JSONObject()); // Pass the answers here
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create Volley request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Send POST request using JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, requestParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Extract career field and college from the response
                            String message = response.getString("message");
                            Log.e("ResultActivity", "response "+response.toString());
                            String recommendedField = response.getString("recommended_field");
                            String recommendedCollege = response.getString("recommended_college");

                            // Update the UI with the results
                            resultText.setText("Your Recommended Career: " + recommendedField);
                            collegeText.setText("Recommended College: " + recommendedCollege);
                            Toast.makeText(ResultActivity.this, message, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ResultActivity.this, "Error in response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ResultActivity", "Error message "+error.getMessage());
                        Toast.makeText(ResultActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Add the request to the Volley request queue
        requestQueue.add(jsonObjectRequest);
    }
}
