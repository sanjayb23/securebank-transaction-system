package com.securebank.controller;

import com.securebank.dto.request.CreateAccountRequest;
import com.securebank.dto.response.AccountResponse;
import com.securebank.dto.response.ApiResponse;
import com.securebank.dto.response.TransactionResponse;
import com.securebank.entity.User;
import com.securebank.enums.AccountStatus;
import com.securebank.repository.UserRepository;
import com.securebank.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(@RequestBody CreateAccountRequest request) {
        Long userId = getCurrentUserId();
        AccountResponse response = accountService.createAccount(request, userId);
        return ResponseEntity.ok(ApiResponse.success("Account created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getAccounts() {
        Long userId = getCurrentUserId();
        List<AccountResponse> responses = accountService.getAccountsByUser(userId);
        return ResponseEntity.ok(ApiResponse.success("Accounts retrieved", responses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccount(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        AccountResponse response = accountService.getAccountById(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Account details retrieved", response));
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<ApiResponse<BigDecimal>> getBalance(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        BigDecimal balance = accountService.getAccountBalance(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Balance retrieved", balance));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Object>> getDashboard() {
        Long userId = getCurrentUserId();
        Object dashboard = accountService.getUserDashboard(userId);
        return ResponseEntity.ok(ApiResponse.success("Dashboard data retrieved", dashboard));
    }

    @GetMapping("/{id}/statement")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getStatement(@PathVariable Long id, Pageable pageable) {
        Long userId = getCurrentUserId();
        Page<TransactionResponse> page = accountService.getAccountStatement(id, userId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Statement retrieved", page));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<String>> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        Long userId = getCurrentUserId();
        AccountStatus status = AccountStatus.valueOf(request.get("status"));
        accountService.updateAccountStatus(id, status, userId);
        return ResponseEntity.ok(ApiResponse.success("Account status updated", null));
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }
}