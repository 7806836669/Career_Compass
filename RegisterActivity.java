package com.sruthi.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etAge, etEmail, etPassword;
    private Button btnRegister;
    private ImageView btnBack;
    private RequestQueue requestQueue;

    private static final String REGISTER_URL = Ipv4Connection.getBaseUrl() + "register.php"; // replace with your local URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnBack = findViewById(R.id.btnBack);

        requestQueue = Volley.newRequestQueue(this);

        btnBack.setOnClickListener(v -> finish());

        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String age = etAge.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(age) ||
                    TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            } else {
                registerUser(name, age, email, password);
            }
        });
    }

    private void registerUser(String name, String age, String email, String password) {
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("name", name);
            jsonBody.put("age", age);
            jsonBody.put("email", email);
            jsonBody.put("password", password);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, REGISTER_URL, jsonBody,
                    response -> {
                        try {
                            String status = response.getString("status");
                            String message = response.getString("message");
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                            if (status.equalsIgnoreCase("success")) {
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Response error", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> Toast.makeText(this, "Volley Error: " + error.toString(), Toast.LENGTH_LONG).show());

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
