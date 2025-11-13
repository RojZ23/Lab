package com.example.lab;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AIService {

    private final WebClient client;
    private final String apiKey;
    private final String model;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AIService(
            @Value("${spring.ai.openai.api-key}") String apiKey,
            @Value("${spring.ai.openai.base-url}") String baseUrl,
            @Value("${spring.ai.openai.chat.options.model}") String model
    ) {
        this.apiKey = apiKey;
        this.model = model;
        this.client = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public String generateAsset(String prompt) {
        String requestBody = """
        {
          "model": "%s",
          "messages": [
            {"role": "system", "content": "You are an expert AI assistant that creates organized study guides with summaries, key concepts, and definitions."},
            {"role": "user", "content": "Create a detailed study guide about the following topic: %s. Structure it with headings and bullet points."}
          ]
        }
        """.formatted(model, prompt);

        try {
            String responseJson = client.post()
                    .uri("/chat/completions")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (responseJson == null || responseJson.isBlank()) {
                return "No response from AI service.";
            }

            JsonNode root = objectMapper.readTree(responseJson);
            JsonNode contentNode = root.path("choices").get(0).path("message").path("content");

            if (contentNode.isMissingNode()) {
                return "Unexpected response structure from AI.";
            }

            return contentNode.asText();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error contacting AI service: " + e.getMessage();
        }
    }
}
