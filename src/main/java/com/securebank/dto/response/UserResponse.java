package com.securebank.dto.response;

import com.securebank.enums.UserRole;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private UserRole role;
    private Boolean isVerified;
    private LocalDateTime createdAt;

    public String getUsername() {
        return username;
    }
}