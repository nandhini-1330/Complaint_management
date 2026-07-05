package com.complaintms.complaint_management_system.controller;

import com.complaintms.complaint_management_system.entity.Faq;
import com.complaintms.complaint_management_system.service.FaqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/faqs")
@CrossOrigin(origins = "*")
public class FaqController {

    @Autowired
    private FaqService faqService;

    @GetMapping
    public List<Faq> getAll() {
        return faqService.getAllFaqs();
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generate() {
        try {
            return ResponseEntity.ok(faqService.generateFaqs());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}