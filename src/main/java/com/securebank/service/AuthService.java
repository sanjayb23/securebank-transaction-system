package com.securebank.service;

import com.securebank.dto.request.LoginRequest;
import com.securebank.dto.request.RegisterRequest;
import com.securebank.dto.response.AuthResponse;
import com.securebank.dto.response.UserResponse;
import com.securebank.entity.AuditLog;
import com.securebank.repository.AuditLogRepository;
import com.securebank.entity.User;
import com.securebank.enums.UserRole;
import com.securebank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuditLogRepository auditLogRepository;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .role(UserRole.USER)
                .isVerified(false)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user);
        UserResponse userResponse = mapToUserResponse(user);
        return new AuthResponse(token, userResponse);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user);
        
        // Create audit log
        AuditLog auditLog = AuditLog.builder()
                .user(user)
                .action("USER_LOGIN")
                .entityType("USER")
                .entityId(user.getId())
                .ipAddress("127.0.0.1") // TODO: Extract from request
                .build();
        auditLogRepository.save(auditLog);
        
        UserResponse userResponse = mapToUserResponse(user);
        return new AuthResponse(token, userResponse);
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());
        response.setIsVerified(user.getIsVerified());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}