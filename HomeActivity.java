package com.sruthi.myapp;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity {

    private EditText etName;
    private Spinner spinnerGender, spinnerCategory;
    private Button btnNext;
    private ImageView btnBack, menuIcon;

    private String Name;

    private static final String SEND_OTP_URL = Ipv4Connection.getBaseUrl() + "Home.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize UI elements
        etName = findViewById(R.id.et_name);
        spinnerGender = findViewById(R.id.spinner_gender);
        spinnerCategory = findViewById(R.id.spinner_category);
        btnNext = findViewById(R.id.btn_next);
        btnBack = findViewById(R.id.btn_back);
        menuIcon = findViewById(R.id.menu_icon); // ic_option icon

        Name = etName.getText().toString().trim();
        Log.d("TAG","Name is :" + Name);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Name = etName.getText().toString().trim();
//                sendData();
                Intent intent = new Intent(HomeActivity.this, QuestionsActivity.class);
                intent.putExtra("user_name" , "" + Name);
                startActivity(intent);
                finish();
            }
        });

        // Populate Gender Spinner
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(
                this, R.array.gender_options, android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        // Populate Category Spinner
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this, R.array.category_options, android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // Disable Next Button Initially
//        btnNext.setEnabled(false);

        // Listeners for input validation
        etName.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateInputs();
            }
        });

        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                validateInputs();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                validateInputs();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Back Button Listener
        btnBack.setOnClickListener(view -> finish());

        // Menu Icon (Navigates to About Page)
        menuIcon.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, AboutActivity.class);
            startActivity(intent);
        });

        // Next Button Click
    }

    // Validate Inputs: Enable Next button only if all fields are selected
    private void validateInputs() {
        String name = etName.getText().toString().trim();
        boolean isGenderSelected = true;
        boolean isCategorySelected = true;

        btnNext.setEnabled(!name.isEmpty() && isGenderSelected && isCategorySelected);
    }

    private void sendData() {
        String name = etName.getText().toString().trim();
        String gender = spinnerGender.getSelectedItem().toString();
        String category = spinnerCategory.getSelectedItem().toString();

        // Validate before sending
        if (name.isEmpty() || gender.equals("Select Gender") || category.equals("Select Category")) {
            return;
        }

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("name", name);
            jsonBody.put("gender", gender);
            jsonBody.put("category", category);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    SEND_OTP_URL,
                    jsonBody,
                    response -> {
                        try {
                            if (response.getString("status").equals("success")) {
                                // Continue to next activity
                                Intent intent = new Intent(HomeActivity.this, QuestionsActivity.class);
                                startActivity(intent);
                            } else {
                                // Show error message
                                Toast.makeText(HomeActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        Toast.makeText(HomeActivity.this, "Network error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
            );

            // Add request to the queue
            Volley.newRequestQueue(this).add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
