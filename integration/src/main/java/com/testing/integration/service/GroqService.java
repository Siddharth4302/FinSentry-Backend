package com.testing.integration.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class GroqService {

    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public GroqService(
            @Value("${groq.api-key}") String apiKey,
            @Value("${groq.model}") String model) {

        this.apiKey = apiKey;
        this.model = model;

        this.restClient = RestClient.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .build();
    }

    // Reusable method for all agents using Groq
    public String chat(String systemPrompt, String userPrompt) {

        Map<String, Object> request = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of(
                                "role", "system",
                                "content", systemPrompt
                        ),
                        Map.of(
                                "role", "user",
                                "content", userPrompt
                        )
                ),
                "temperature", 0.2,
                "max_completion_tokens", 1024
        );

        Map<?, ?> response = restClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Map.class);

        if (response == null) {
            throw new IllegalStateException(
                    "Groq returned an empty response"
            );
        }

        Object choicesObject = response.get("choices");

        if (!(choicesObject instanceof List<?> choices)
                || choices.isEmpty()) {

            throw new IllegalStateException(
                    "Groq response did not contain any choices"
            );
        }

        Object choiceObject = choices.get(0);

        if (!(choiceObject instanceof Map<?, ?> choice)) {
            throw new IllegalStateException(
                    "Groq returned an invalid choice"
            );
        }

        Object messageObject = choice.get("message");

        if (!(messageObject instanceof Map<?, ?> message)) {
            throw new IllegalStateException(
                    "Groq response did not contain a valid message"
            );
        }

        Object content = message.get("content");

        if (content == null || content.toString().isBlank()) {
            throw new IllegalStateException(
                    "Groq returned empty message content"
            );
        }

        return content.toString();
    }

    // Existing connection test
    public String testConnection() {

        return chat(
                "You are a helpful assistant.",
                "Reply with exactly: Groq connection successful"
        );
    }
}