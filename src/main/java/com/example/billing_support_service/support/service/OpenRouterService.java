package com.example.billing_support_service.support.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenRouterService {

    private static final Logger log = LoggerFactory.getLogger(OpenRouterService.class);

    @Value("${openrouter.api.key:}")
    private String apiKey;

    @Value("${openrouter.model:mistralai/mistral-7b-instruct:free}")
    private String model;

    @Value("${webui.base-url:http://localhost:8081}")
    private String webUiBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";

    public String getChatBotResponse(String userMessage) {
        if (userMessage == null || userMessage.isBlank()) {
            return "Please type a question.";
        }
        if (apiKey == null || apiKey.isBlank()) {
            return "Natasha is not configured yet (missing OPENROUTER_API_KEY).";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("HTTP-Referer", webUiBaseUrl);
        headers.set("X-Title", "NoBroker Clone (Natasha)");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content",
                        "You are Natasha, an AI assistant for the NoBroker real estate website. " +
                                "Answer only property-related questions. Keep answers short (2-3 lines), " +
                                "and use bullet points with '•' symbol with new line, and avoid unnecessary info."),
                Map.of("role", "user", "content", userMessage)));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, request, Map.class);
            Map body = response.getBody();
            if (body == null) {
                log.warn("OpenRouter returned empty body");
                return "Natasha error: empty response from OpenRouter.";
            }
            Object choicesObj = body.get("choices");
            if (!(choicesObj instanceof List<?> choices) || choices.isEmpty()) {
                log.warn("OpenRouter returned unexpected choices: {}", choicesObj);
                return "Natasha error: unexpected response from OpenRouter.";
            }
            Map<String, Object> choice = (Map<String, Object>) choices.get(0);
            Map<String, Object> message = (Map<String, Object>) choice.get("message");
            Object content = message != null ? message.get("content") : null;
            return content != null ? content.toString() : "Natasha error: missing content in response.";
        } catch (HttpStatusCodeException e) {
            String resp = e.getResponseBodyAsString();
            String clipped = resp == null ? "" : resp.replaceAll("\\s+", " ");
            if (clipped.length() > 200) clipped = clipped.substring(0, 200) + "...";
            log.warn("OpenRouter HTTP {}: {}", e.getStatusCode().value(), clipped);
            return "Natasha error: OpenRouter returned HTTP " + e.getStatusCode().value() + ". Check API key/model.";
        } catch (Exception e) {
            log.warn("OpenRouter request failed: {}", e.toString());
            return "Natasha error: failed to reach OpenRouter (check internet/firewall/DNS).";
        }
    }
}
