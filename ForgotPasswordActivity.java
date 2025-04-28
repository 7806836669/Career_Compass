package com.sruthi.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.*;
import com.android.volley.toolbox.*;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailInput, otpInput;
    private Button sendOtpBtn, verifyOtpBtn;
    private LinearLayout otpLayout;
    private ImageView backArrow;

    private RequestQueue requestQueue;

    private static final String SEND_OTP_URL = Ipv4Connection.getBaseUrl() + "forgot_password.php";
    private static final String VERIFY_OTP_URL = Ipv4Connection.getBaseUrl() + "verify_forgot_otp.php";

    private String emailText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailInput = findViewById(R.id.phoneInput);  // this should be updated to `emailInput` in layout
        otpInput = findViewById(R.id.otpInput);
        sendOtpBtn = findViewById(R.id.sendOtpBtn);
        verifyOtpBtn = findViewById(R.id.verifyOtpBtn);
        otpLayout = findViewById(R.id.otpLayout);
        backArrow = findViewById(R.id.backArrow);

        requestQueue = Volley.newRequestQueue(this);

        backArrow.setOnClickListener(v -> finish());

        sendOtpBtn.setOnClickListener(v -> {
            emailText = emailInput.getText().toString().trim();

            if (TextUtils.isEmpty(emailText) || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                emailInput.setError("Enter a valid email address");
                return;
            }

            sendOtpToEmail(emailText);
        });

        verifyOtpBtn.setOnClickListener(v -> {
            String enteredOtp = otpInput.getText().toString().trim();

            if (TextUtils.isEmpty(enteredOtp)) {
                otpInput.setError("Enter OTP");
                return;
            }

            verifyOtp(emailText, enteredOtp);
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

                            if (status.equalsIgnoreCase("success")) {
                                otpLayout.setVisibility(View.VISIBLE);
                                verifyOtpBtn.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            Toast.makeText(this, "Response error", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> Toast.makeText(this, "Volley error: " + error.toString(), Toast.LENGTH_LONG).show()
            );

            request.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

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

                            if (status.equalsIgnoreCase("success")) {
                                Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                                intent.putExtra("email", email); // pass email to ResetPasswordActivity
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(this, "Parsing error", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> Toast.makeText(this, "Volley error: " + error.toString(), Toast.LENGTH_LONG).show()
            );

            request.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            requestQueue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
