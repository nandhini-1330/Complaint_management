package com.complaintms.complaint_management_system.controller;

import com.complaintms.complaint_management_system.entity.User;
import com.complaintms.complaint_management_system.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        try {
            User user = authService.register(
                    body.get("name"),
                    body.get("email"),
                    body.get("password"),
                    body.getOrDefault("role", "AGENT")
            );
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("name", user.getName());
            response.put("email", user.getEmail());
            response.put("role", user.getRole());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
    try {
        Map<String, String> result = authService.login(body.get("email"), body.get("password"));
        return ResponseEntity.ok(result);
    } catch (RuntimeException e) {
        return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
    }
}
}
