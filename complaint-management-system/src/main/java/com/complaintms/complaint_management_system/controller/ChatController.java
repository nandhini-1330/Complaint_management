package com.complaintms.complaint_management_system.controller;

import com.complaintms.complaint_management_system.service.GroqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private GroqService groqService;

    @PostMapping
    public ResponseEntity<?> chat(@RequestBody Map<String, String> body) {
        try {
            String userMessage = body.get("message");

            String prompt = """
                You are the Resolve Assistant — a helpful AI chat assistant embedded on a complaint management system website called "Resolve".
                Resolve lets citizens file complaints (safety, facility, billing, technical, customer service issues), and uses AI to score urgency and route each complaint to the right department automatically.

                Answer the user's message briefly (2-4 sentences max), in a friendly and helpful tone.
                If they ask about checking a complaint's status, tell them to use the "File a Complaint" form to get a ticket ID, and that an agent will update its status on the dashboard.
                If they ask something unrelated to complaints or the platform, answer briefly and naturally anyway.

                User message: %s
                """.formatted(userMessage);

            String reply = groqService.callGroq(prompt);
            return ResponseEntity.ok(Map.of("reply", reply));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "AI is unavailable right now: " + e.getMessage()));
        }
    }
}