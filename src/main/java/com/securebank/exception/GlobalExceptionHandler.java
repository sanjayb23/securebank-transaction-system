package com.securebank.exception;

import com.securebank.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleInsufficientBalance(InsufficientBalanceException ex) {
        Map<String, Object> details = new HashMap<>();
        details.put("available", ex.getAvailable());
        details.put("required", ex.getRequired());
        details.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(ex.getMessage(), details));
    }

    @ExceptionHandler(AccountFrozenException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleAccountFrozen(AccountFrozenException ex) {
        Map<String, Object> details = new HashMap<>();
        details.put("errorCode", "ACCOUNT_FROZEN");
        details.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ex.getMessage(), details));
    }

    @ExceptionHandler(DailyLimitExceededException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleDailyLimitExceeded(DailyLimitExceededException ex) {
        Map<String, Object> details = new HashMap<>();
        details.put("errorCode", "DAILY_LIMIT_EXCEEDED");
        details.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(ex.getMessage(), details));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<String>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Invalid username or password"));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleUsernameNotFound(UsernameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Invalid username or password"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("Validation failed", errors));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(ex.getMessage()));
    }
}