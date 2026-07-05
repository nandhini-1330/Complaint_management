package com.complaintms.complaint_management_system.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AnthropicService {

    @Value("${anthropic.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public AnthropicService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private static final String ANTHROPIC_URL = "https://api.anthropic.com/v1/messages";

    public String callClaude(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);
        headers.set("anthropic-version", "2023-06-01");

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "claude-sonnet-4-5-20250929");
        requestBody.put("max_tokens", 200);
        requestBody.put("messages", List.of(message));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        Map response = restTemplate.postForObject(ANTHROPIC_URL, request, Map.class);

        try {
            List content = (List) response.get("content");
            Map firstBlock = (Map) content.get(0);
            return (String) firstBlock.get("text");
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

        String result = callClaude(prompt);
        return result.trim().toUpperCase().replaceAll("[^A-Z]", "");
    }

    public String getCategory(String title, String description) {
        String prompt = """
            Classify this complaint into ONE category from: BILLING, TECHNICAL, SAFETY, CUSTOMER_SERVICE, FACILITY, OTHER.
            
            Complaint Title: %s
            Complaint Description: %s
            
            Respond with ONLY the category word, nothing else.
            """.formatted(title, description);

        String result = callClaude(prompt);
        return result.trim().toUpperCase().replaceAll("[^A-Z_]", "");
    }

    public String getSummary(String title, String description) {
        String prompt = """
            Summarize this complaint in exactly 2 short sentences for an agent dashboard. Be professional and concise.
            
            Title: %s
            Description: %s
            """.formatted(title, description);

        return callClaude(prompt).trim();
    }
}