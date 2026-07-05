package com.complaintms.complaint_management_system.service;

import com.complaintms.complaint_management_system.entity.User;
import com.complaintms.complaint_management_system.repository.UserRepository;
import com.complaintms.complaint_management_system.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public User register(String name, String email, String password, String role) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        User user = new User(name, email, passwordEncoder.encode(password), role);
        return userRepository.save(user);
    }
    
public Map<String, String> login(String email, String password) {
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Invalid email or password"));

    if (!passwordEncoder.matches(password, user.getPassword())) {
        throw new RuntimeException("Invalid email or password");
    }

    String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

    Map<String, String> result = new HashMap<>();
    result.put("token", token);
    result.put("role", user.getRole());
    result.put("name", user.getName());
    return result;
}
}