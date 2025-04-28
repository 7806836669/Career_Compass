package com.sruthi.myapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText etEmail, etNewPassword;
    private Button btnResetPassword;
    private ImageView btnBack;
    private ProgressDialog progressDialog;

    private static final String RESET_PASSWORD_URL = Ipv4Connection.getBaseUrl() + "reset_password.php"; // Change this to your actual API URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Enable back button in ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Reset Password");
        }

        etEmail = findViewById(R.id.etEmail);
        etNewPassword = findViewById(R.id.etNewPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnBack = findViewById(R.id.btnBack);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Resetting Password...");

        // Button Click Listener
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // Handle Back Button Click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Navigate to ForgotPasswordActivity when back button is pressed
            startActivity(new Intent(ResetPasswordActivity.this, ForgotPasswordActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void resetPassword() {
        String email = etEmail.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();

        Log.d("Reset Password", "Email: " + email);
        Log.d("Reset Password", "Password: " + newPassword);

        if (email.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(ResetPasswordActivity.this, "Email and New Password are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        // Create JSON object to match PHP input
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("email", email);
            requestData.put("new_password", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
            progressDialog.dismiss();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                RESET_PASSWORD_URL,
                requestData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        try {
                            String status = response.getString("status");
                            String message = response.getString("message");

                            Toast.makeText(ResetPasswordActivity.this, message, Toast.LENGTH_LONG).show();

                            if (status.equals("success")) {
                                startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                                finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ResetPasswordActivity.this, "Password reset failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(ResetPasswordActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("ResetPasswordError", error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json"); // Tell server this is JSON
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
}
