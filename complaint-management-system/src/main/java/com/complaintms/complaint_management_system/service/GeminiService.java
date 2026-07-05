package com.complaintms.complaint_management_system.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public GeminiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private static final String GEMINI_URL =
    "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";

    public String callGemini(String prompt) {
        String url = GEMINI_URL + apiKey;

        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", prompt);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(textPart));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(content));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        Map response = restTemplate.postForObject(url, request, Map.class);

        try {
            List candidates = (List) response.get("candidates");
            Map firstCandidate = (Map) candidates.get(0);
            Map contentObj = (Map) firstCandidate.get("content");
            List parts = (List) contentObj.get("parts");
            Map firstPart = (Map) parts.get(0);
            return (String) firstPart.get("text");
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

        String result = callGemini(prompt);
        return result.trim().toUpperCase().replaceAll("[^A-Z]", "");
    }

    public String getCategory(String title, String description) {
        String prompt = """
            Classify this complaint into ONE category from: BILLING, TECHNICAL, SAFETY, CUSTOMER_SERVICE, FACILITY, OTHER.
            
            Complaint Title: %s
            Complaint Description: %s
            
            Respond with ONLY the category word, nothing else.
            """.formatted(title, description);

        String result = callGemini(prompt);
        return result.trim().toUpperCase().replaceAll("[^A-Z_]", "");
    }

    public String getSummary(String title, String description) {
        String prompt = """
            Summarize this complaint in exactly 2 short sentences for an agent dashboard. Be professional and concise.
            
            Title: %s
            Description: %s
            """.formatted(title, description);

        return callGemini(prompt).trim();
    }
}