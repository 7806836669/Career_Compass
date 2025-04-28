package com.sruthi.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private EditText usernameEditText, passwordEditText;
    private TextView tvCreateAccount, tvForgotPassword;

    private static final String LOGIN_URL = Ipv4Connection.getBaseUrl() + "login.php";
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        btnLogin = findViewById(R.id.btn_login);
        usernameEditText = findViewById(R.id.et_username);
        passwordEditText = findViewById(R.id.et_password);
        tvCreateAccount = findViewById(R.id.tv_create_account);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);

        requestQueue = Volley.newRequestQueue(this);

        btnLogin.setOnClickListener(view -> validateInputs());
        tvCreateAccount.setOnClickListener(view -> navigateToCreateAccount());
        tvForgotPassword.setOnClickListener(view -> navigateToForgotPassword());
    }

    private void validateInputs() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter Username and Password", Toast.LENGTH_SHORT).show();
        } else {
            loginUser(username, password);
        }
    }

    private void loginUser(String username, String password) {
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("name", username); // match PHP field
            jsonBody.put("password", password);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, LOGIN_URL, jsonBody,
                    response -> {
                        try {
                            String status = response.getString("status");
                            String message = response.getString("message");

                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                            if (status.equalsIgnoreCase("success")) {
                                navigateToHome();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(this, "Parsing error", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> Toast.makeText(this, "Volley error: " + error.toString(), Toast.LENGTH_LONG).show());

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

    private void navigateToHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToCreateAccount() {
        Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
        startActivity(intent);
    }

    private void navigateToForgotPassword() {
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }
}
