
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



    public String testConnection() {
        System.out.println(
                "Truststore: " +
                        System.getProperty("javax.net.ssl.trustStore", "JDK default")
        );
        Map<String, Object> request = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", "Reply with exactly: Groq connection successful"
                        )
                ),
                "temperature", 0.2,
                "max_completion_tokens", 256
        );

        Map<?, ?> response = restClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Map.class);
        System.out.println("Groq raw response: " + response);

        if (response == null) {
            throw new IllegalStateException("Groq returned an empty response");
        }

        List<?> choices = (List<?>) response.get("choices");

        if (choices == null || choices.isEmpty()) {
            throw new IllegalStateException(
                    "Groq response did not contain any choices");
        }

        Map<?, ?> choice = (Map<?, ?>) choices.get(0);
        Map<?, ?> message = (Map<?, ?>) choice.get("message");
        System.out.println("Groq content: " + message.get("content"));
        return String.valueOf(message.get("content"));

    }
}
