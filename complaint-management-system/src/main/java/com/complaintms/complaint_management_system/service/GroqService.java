package com.complaintms.complaint_management_system.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class GroqService {

    @Value("${groq.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public GroqService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";

    public String callGroq(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "llama-3.1-8b-instant");
        requestBody.put("messages", List.of(message));
        requestBody.put("max_tokens", 300);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        Map response = restTemplate.postForObject(GROQ_URL, request, Map.class);

        try {
            List choices = (List) response.get("choices");
            Map firstChoice = (Map) choices.get(0);
            Map messageObj = (Map) firstChoice.get("message");
            return (String) messageObj.get("content");
        } catch (Exception e) {
            return "AI response parsing failed: " + e.getMessage();
        }
    }

    public String getPriority(String title, String description) {
        String prompt = """
            Analyze this complaint and respond with ONLY ONE WORD from these options: LOW, MEDIUM, HIGH, CRITICAL.
            Rules:
            - Safety issues, security threats, legal issues = CRITICAL or HIGH
            - Service disruptions affecting many people = HIGH or MEDIUM
            - Simple queries, minor issues = LOW
            Complaint Title: %s
            Complaint Description: %s
            Respond with ONLY the priority word, nothing else.
            """.formatted(title, description);

        String result = callGroq(prompt);
        return result.trim().toUpperCase().replaceAll("[^A-Z]", "");
    }

    public String getCategory(String title, String description) {
        String prompt = """
            Classify this complaint into ONE category from: BILLING, TECHNICAL, SAFETY, CUSTOMER_SERVICE, FACILITY, OTHER.
            Complaint Title: %s
            Complaint Description: %s
            Respond with ONLY the category word, nothing else.
            """.formatted(title, description);

        String result = callGroq(prompt);
        return result.trim().toUpperCase().replaceAll("[^A-Z_]", "");
    }
}