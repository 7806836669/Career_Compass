package com.sruthi.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sruthi.myapp.model.FieldsQuestion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FieldQuestionsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FieldsQuestionAdapter adapter;
    private List<FieldsQuestion> questionsList = new ArrayList<>();
    private Map<Integer, String> selectedAnswers = new HashMap<>();
    private Button submitButton;
    private ImageView backArrow;

    String TAG  = FieldQuestionsActivity.class.getSimpleName();
    private String Name;
    private String selectedField;

    private static final String FETCH_FIELD_QUESTIONS_URL = Ipv4Connection.getBaseUrl() + "get_confused_questions.php";
    private static final String SUBMIT_FIELD_ANSWERS_URL = Ipv4Connection.getBaseUrl() + "store_confused_answers.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_specific_questions);

        recyclerView = findViewById(R.id.recyclerView);
        submitButton = findViewById(R.id.submitButton);
        backArrow = findViewById(R.id.ic_back_arrow);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FieldsQuestionAdapter(questionsList, selectedAnswers);
        recyclerView.setAdapter(adapter);

        // Get values passed from previous activity
        Name = getIntent().getStringExtra("user_name");
        selectedField = getIntent().getStringExtra("selected_field");

        if (Name == null || selectedField == null) {
            Toast.makeText(this, "User or field not found. Try again!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        backArrow.setOnClickListener(v -> {
            startActivity(new Intent(FieldQuestionsActivity.this, QuestionsActivity.class));
            finish();
        });

        submitButton.setOnClickListener(v -> submitAnswers());

        fetchFieldQuestions();
    }

    private void fetchFieldQuestions() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("field_name", selectedField);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error preparing request", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                FETCH_FIELD_QUESTIONS_URL+"?field_name="+selectedField,
                requestBody,
                response -> {
                    try {
                        Log.e(TAG, response.toString());
                        JSONArray questionsArray = response.getJSONArray("questions");

                        questionsList.clear(); // Clear existing data

                        for (int i = 0; i < questionsArray.length(); i++) {
                            JSONObject qObj = questionsArray.getJSONObject(i);
                            int id = qObj.getInt("id");
                            String questionText = qObj.getString("question");

                            JSONObject options = qObj.getJSONObject("options");
                            String optionA = options.optString("A", "Option A");
                            String optionB = options.optString("B", "Option B");
                            String optionC = options.optString("C", "Option C");
                            String optionD = options.optString("D", "Option D");

                            questionsList.add(new FieldsQuestion(id, questionText, optionA, optionB, optionC, optionD));
                        }

                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing server response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Fetch failed: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(request);
    }

    private void submitAnswers() {
        if (selectedAnswers.size() < questionsList.size()) {
            Toast.makeText(this, "Please answer all questions!", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject jsonBody = new JSONObject();
        JSONObject answersJson = new JSONObject();

        try {
            for (Map.Entry<Integer, String> entry : selectedAnswers.entrySet()) {
                answersJson.put(String.valueOf(entry.getKey()), entry.getValue());
            }

            jsonBody.put("user_name", Name);
            jsonBody.put("field_name", selectedField);
            jsonBody.put("answers", answersJson);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error preparing submission", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                SUBMIT_FIELD_ANSWERS_URL,
                jsonBody,
                response -> {
                    try {
                        String FieldName = response.getString("field_name");
                        String message = response.getString("message");
                        Toast.makeText(FieldQuestionsActivity.this, message, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(FieldQuestionsActivity.this, RecommendationActivity.class);
                        intent.putExtra("user_name", Name);
                        intent.putExtra("selected_field", selectedField);
                        intent.putExtra("field_name", FieldName);
                        intent.putExtra("message", message);
                        startActivity(intent);
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(FieldQuestionsActivity.this, "Server response error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(FieldQuestionsActivity.this, "Submission failed: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(request);
    }
}
