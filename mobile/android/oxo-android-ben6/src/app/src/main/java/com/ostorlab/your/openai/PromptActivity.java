package com.ostorlab.your.openai;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PromptActivity extends AppCompatActivity {

    private EditText promptEditText;
    private Button sendButton;

    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prompt);

        promptEditText = findViewById(R.id.promptEditText);
        sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(view -> {
            String prompt = promptEditText.getText().toString().trim();
            if (!prompt.isEmpty()) {
                String token = readTokenFromAppExternalStorage();
                if (token == null || token.isEmpty()) {
                    Toast.makeText(PromptActivity.this, "Token not found, please login again", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                callOpenAIAPI(token, prompt);
            } else {
                Toast.makeText(PromptActivity.this, "Please enter a prompt", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String readTokenFromAppExternalStorage() {
        File file = new File(getExternalFilesDir(null), "openai_token.txt");
        if (!file.exists()) return null;

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] bytes = new byte[(int) file.length()];
            fis.read(bytes);
            return new String(bytes, StandardCharsets.UTF_8).trim();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void callOpenAIAPI(String token, String prompt) {
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("model", "text-davinci-003");
            jsonBody.put("prompt", prompt);
            jsonBody.put("max_tokens", 50);

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(jsonBody.toString(), JSON);

            Request request = new Request.Builder()
                    .url("https://api.openai.com/v1/completions")
                    .addHeader("Authorization", "Bearer " + token)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> navigateToResult("We are not able to get an answer"));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        runOnUiThread(() -> navigateToResult("We are not able to get an answer"));
                        return;
                    }

                    String responseBody = response.body().string();

                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        JSONArray choices = jsonResponse.getJSONArray("choices");
                        String answer = choices.getJSONObject(0).getString("text").trim();

                        runOnUiThread(() -> navigateToResult(answer));
                    } catch (Exception e) {
                        runOnUiThread(() -> navigateToResult("We are not able to get an answer"));
                    }
                }
            });
        } catch (Exception e) {
            navigateToResult("We are not able to get an answer");
        }
    }

    private void navigateToResult(String result) {
        Intent intent = new Intent(PromptActivity.this, ResultActivity.class);
        intent.putExtra("result", result);
        startActivity(intent);
    }
}
