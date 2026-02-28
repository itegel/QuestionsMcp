package com.codingagent.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class QwenClient {

    private final OkHttpClient client;
    private final String apiUrl;
    private final String apiKey;
    private final String model;
    private final int timeout;
    private final ObjectMapper objectMapper;

    public QwenClient() {
        this.apiUrl = ConfigLoader.getProperty("qwen.api.url") + "/chat/completions";
        this.apiKey = ConfigLoader.getProperty("qwen.api.api-key");
        this.model = ConfigLoader.getProperty("qwen.api.model", "qwen3.5-plus");
        this.timeout = ConfigLoader.getIntProperty("qwen.timeout", 60000);
        this.client = new OkHttpClient.Builder()
                .connectTimeout(timeout, java.util.concurrent.TimeUnit.MILLISECONDS)
                .readTimeout(timeout, java.util.concurrent.TimeUnit.MILLISECONDS)
                .writeTimeout(timeout, java.util.concurrent.TimeUnit.MILLISECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public String chat(String prompt) {
        QwenRequest.Message message = new QwenRequest.Message("user", prompt);
        QwenRequest request = new QwenRequest(model, Arrays.asList(message));

        try {
            String requestBody = objectMapper.writeValueAsString(request);
            Request httpRequest = new Request.Builder()
                    .url(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                    .build();

            Response response = client.newCall(httpRequest).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                QwenResponse qwenResponse = objectMapper.readValue(responseBody, QwenResponse.class);
                if (qwenResponse.getChoices() != null && !qwenResponse.getChoices().isEmpty()) {
                    return qwenResponse.getChoices().get(0).getMessage().getContent();
                }
            } else {
                System.err.println("API request failed: " + response.code() + " " + response.message());
                try {
                    String errorBody = response.body().string();
                    System.err.println("Error response: " + errorBody);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "No response from Qwen API";
    }

    public String chatWithContext(List<QwenRequest.Message> messages) {
        QwenRequest request = new QwenRequest(model, messages);

        try {
            String requestBody = objectMapper.writeValueAsString(request);
            Request httpRequest = new Request.Builder()
                    .url(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                    .build();

            Response response = client.newCall(httpRequest).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                QwenResponse qwenResponse = objectMapper.readValue(responseBody, QwenResponse.class);
                if (qwenResponse.getChoices() != null && !qwenResponse.getChoices().isEmpty()) {
                    return qwenResponse.getChoices().get(0).getMessage().getContent();
                }
            } else {
                System.err.println("API request failed: " + response.code() + " " + response.message());
                try {
                    String errorBody = response.body().string();
                    System.err.println("Error response: " + errorBody);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "No response from Qwen API";
    }

}
