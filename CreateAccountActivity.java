package com.sruthi.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.DefaultRetryPolicy;
import org.json.JSONException;
import org.json.JSONObject;

public class CreateAccountActivity extends AppCompatActivity {
    private EditText etEmail, etOtp;
    private Button btnSendOtp, btnVerifyOtp;
    private RequestQueue requestQueue;

    // ✅ Replace with your actual IP and folder name (no spaces)
    private static final String SEND_OTP_URL = Ipv4Connection.getBaseUrl() + "sendmail.php";
    private static final String VERIFY_OTP_URL = Ipv4Connection.getBaseUrl() + "verify_otp.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        etEmail = findViewById(R.id.etEmail);
        etOtp = findViewById(R.id.etOtp);
        btnSendOtp = findViewById(R.id.btnSendOtp);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        ImageView backArrow = findViewById(R.id.backArrow);

        requestQueue = Volley.newRequestQueue(this);

        backArrow.setOnClickListener(v -> finish());

        btnSendOtp.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Enter Email");
                return;
            }
            sendOtpToEmail(email);
        });

        btnVerifyOtp.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String otp = etOtp.getText().toString().trim();
            if (TextUtils.isEmpty(otp)) {
                etOtp.setError("Enter OTP");
                return;
            }
            verifyOtp(email, otp);
        });
    }

    private void sendOtpToEmail(String email) {
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("email", email);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, SEND_OTP_URL, jsonBody,
                    response -> {
                        try {
                            String status = response.getString("status");
                            String message = response.getString("message");
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Response parsing error", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> Toast.makeText(this, "Error: " + error.toString(), Toast.LENGTH_LONG).show());
                    request.setRetryPolicy(new DefaultRetryPolicy(
                    10000, // 10 seconds timeout
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void verifyOtp(String email, String otp) {
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("email", email);
            jsonBody.put("otp", otp);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, VERIFY_OTP_URL, jsonBody,
                    response -> {
                        try {
                            String status = response.getString("status");
                            String message = response.getString("message");
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                            // ✅ Navigate to next activity if success
                            if (status.equalsIgnoreCase("success")) {
                                Intent intent = new Intent(CreateAccountActivity.this, RegisterActivity.class); // Replace with your next Activity
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Response parsing error", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> Toast.makeText(this, "Error: " + error.toString(), Toast.LENGTH_LONG).show());
            request.setRetryPolicy(new DefaultRetryPolicy(
                    10000, // 10 seconds timeout
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
