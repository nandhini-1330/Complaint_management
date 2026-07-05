package com.complaintms.complaint_management_system.service;

import com.complaintms.complaint_management_system.entity.Complaint;
import com.complaintms.complaint_management_system.entity.Faq;
import com.complaintms.complaint_management_system.repository.ComplaintRepository;
import com.complaintms.complaint_management_system.repository.FaqRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FaqService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private FaqRepository faqRepository;

    @Autowired
    private GroqService groqService;

    public List<Faq> getAllFaqs() {
        return faqRepository.findAll();
    }

    public List<Faq> generateFaqs() {
        List<Complaint> resolved = complaintRepository.findAll().stream()
                .filter(c -> "RESOLVED".equalsIgnoreCase(c.getStatus()) || "CLOSED".equalsIgnoreCase(c.getStatus()))
                .collect(Collectors.toList());

        if (resolved.isEmpty()) {
            throw new RuntimeException("No resolved complaints yet — resolve a few complaints first, then generate FAQs.");
        }

        StringBuilder sb = new StringBuilder();
        for (Complaint c : resolved) {
            sb.append("- [").append(c.getCategory()).append("] ")
              .append(c.getTitle()).append(": ").append(c.getDescription()).append("\n");
        }

        String prompt = """
            Below is a list of resolved complaints from a civic/company grievance system.
            Based on common patterns, generate up to 6 frequently asked questions (FAQ) with clear, helpful, general answers that could help future users with similar issues.

            Respond ONLY with a valid JSON array, no markdown formatting, no extra text, in this exact format:
            [{"question": "...", "answer": "...", "category": "..."}]

            Resolved complaints:
            %s
            """.formatted(sb.toString());

        String raw = groqService.callGroq(prompt);
        String cleaned = raw.trim()
                .replaceAll("(?i)^```json", "")
                .replaceAll("^```", "")
                .replaceAll("```$", "")
                .trim();

        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, String>> parsed = mapper.readValue(cleaned, new TypeReference<List<Map<String, String>>>() {});

            faqRepository.deleteAll();
            List<Faq> saved = new ArrayList<>();
            for (Map<String, String> item : parsed) {
                Faq faq = new Faq(item.get("question"), item.get("answer"), item.getOrDefault("category", "GENERAL"));
                saved.add(faqRepository.save(faq));
            }
            return saved;
        } catch (Exception e) {
            throw new RuntimeException("Could not parse AI response — try generating again.");
        }
    }
}