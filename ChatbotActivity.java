package com.sruthi.myapp;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class ChatbotActivity extends AppCompatActivity {

    private LinearLayout messageContainer;
    private EditText userMessage;
    private Button sendBtn;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        messageContainer = findViewById(R.id.messageContainer);
        userMessage = findViewById(R.id.userMessage);
        sendBtn = findViewById(R.id.sendBtn);
        scrollView = findViewById(R.id.scrollView);

        sendBtn.setOnClickListener(view -> {
            String question = userMessage.getText().toString().trim();
            if (!question.isEmpty()) {
                addMessage("You: " + question);
                userMessage.setText("");
                sendToCohereAPI(question);
            }
        });
    }

    private void sendToCohereAPI(String question) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.cohere.ai/v1/generate";

        JSONObject jsonBody = new JSONObject();
        try {
//            jsonBody.put("model", "command-nightly");
            jsonBody.put("prompt", question);
            jsonBody.put("max_tokens", 100);
            jsonBody.put("temperature", 0.7);
//            jsonBody.put("stop_sequences", new JSONArray()); // optional but valid
//            jsonBody.put("return_likelihoods", "NONE"); // optional but valid
        } catch (Exception e) {
            addMessage("Bot: Error creating request");
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, jsonBody,
                response -> {
                    try {
                        JSONArray generations = response.getJSONArray("generations");
                        String reply = generations.getJSONObject(0).getString("text");
                        addMessage("Bot: " + reply.trim());
//                        Toast.makeText(this, reply, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        addMessage("Bot: Error parsing response");
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    addMessage("Bot: Error: " + error.networkResponse.statusCode + " - " + error.getMessage());
                    error.printStackTrace();
                    Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer 3MRiDx3kVdFiJWuWOsMJNGUvqKaej9CYFNvhPtkZ");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        queue.add(request);
    }

    private void addMessage(String message) {
        View view;

        if (message.startsWith("You:")) {
            view = getLayoutInflater().inflate(R.layout.sent_message, null);
            TextView messageText = view.findViewById(R.id.sentMessageText);
            messageText.setText(message.substring(4).trim());
        } else if (message.startsWith("Bot:")) {
            view = getLayoutInflater().inflate(R.layout.received_messages, null);
            TextView messageText = view.findViewById(R.id.receivedMessageText);
            messageText.setText(message.substring(4).trim());
        } else {
            // fallback
            TextView textView = new TextView(this);
            textView.setText(message);
            view = textView;
        }

        messageContainer.addView(view);
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

}
