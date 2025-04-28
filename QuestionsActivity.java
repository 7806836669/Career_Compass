package com.sruthi.myapp;

import android.content.Intent;
import android.os.Bundle;
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
import com.sruthi.myapp.model.Question;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private QuestionsAdapter adapter;
    private List<Question> questionsList = new ArrayList<>();
    private Map<Integer, String> selectedAnswers = new HashMap<>();
    private Button submitButton;
    private ImageView backArrow;

    private String Name;

    private static final String FETCH_QUESTIONS_URL = Ipv4Connection.getBaseUrl() + "get_questions.php";
    private static final String SUBMIT_ANSWERS_URL = Ipv4Connection.getBaseUrl() + "store_answers.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        recyclerView = findViewById(R.id.recyclerView);
        submitButton = findViewById(R.id.submitButton);
        backArrow = findViewById(R.id.ic_back_arrow);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new QuestionsAdapter(questionsList, selectedAnswers);
        recyclerView.setAdapter(adapter);

        // Back button logic
        backArrow.setOnClickListener(v -> {
            startActivity(new Intent(QuestionsActivity.this, HomeActivity.class));
            finish();
        });

        Intent intent = getIntent();
        // Get name from intent
        Name = intent.getStringExtra("user_name");

        if (Name == null) {
            Toast.makeText(this, "Username not passed!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        submitButton.setOnClickListener(view -> submitAnswers());

        fetchQuestionsFromAPI();
    }

    private void fetchQuestionsFromAPI() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                FETCH_QUESTIONS_URL,
                null,
                response -> {
                    try {
                        if (response != null && response.has("questions")) {
                            JSONArray questionsArray = response.getJSONArray("questions");

                            for (int i = 0; i < questionsArray.length(); i++) {
                                JSONObject questionObj = questionsArray.getJSONObject(i);
                                String idStr = questionObj.getString("id");
                                int id = Integer.parseInt(idStr);
                                String questionText = questionObj.getString("question");

                                JSONObject options = questionObj.getJSONObject("options");
                                String optionA = options.getString("a");
                                String optionB = options.getString("b");
                                String optionC = options.getString("c");
                                String optionD = options.getString("d");

                                questionsList.add(new Question(id, questionText, optionA, optionB, optionC, optionD));
                            }

                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "Failed to load questions", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Failed: " + error.getMessage(), Toast.LENGTH_LONG).show()
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
                String answer = entry.getValue().toLowerCase();
                answersJson.put(String.valueOf(entry.getKey()), answer);
            }
            jsonBody.put("user_name", Name);
            jsonBody.put("answers", answersJson);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error forming data", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, SUBMIT_ANSWERS_URL, jsonBody,
                response -> {
                    try {
                        if (response != null && response.has("message") && response.has("recommended_field") && response.has("recommended_college")) {
                            String message = response.getString("message");
                            String field = response.getString("recommended_field");
                            String college = response.getString("recommended_college");

                            Toast.makeText(QuestionsActivity.this, message, Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(QuestionsActivity.this, ResultActivity.class);
                            intent.putExtra("recommended_field", field);
                            intent.putExtra("recommended_college", college);
                            intent.putExtra("user_name", Name);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(QuestionsActivity.this, "Response missing expected fields", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(QuestionsActivity.this, "Response parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    String errorMsg = (error.networkResponse != null) ? new String(error.networkResponse.data) : error.getMessage();
                    Toast.makeText(QuestionsActivity.this, "Submission failed: " + errorMsg, Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(request);
    }
}
