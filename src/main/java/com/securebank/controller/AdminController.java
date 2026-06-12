package com.securebank.controller;

import com.securebank.dto.response.AdminDashboardResponse;
import com.securebank.dto.response.ApiResponse;
import com.securebank.dto.response.TransactionResponse;
import com.securebank.enums.TransactionStatus;
import com.securebank.enums.TransactionType;
import com.securebank.service.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardResponse>> getDashboard() {
        AdminDashboardResponse dashboard = adminService.getDashboardMetrics();
        return ResponseEntity.ok(ApiResponse.success("Dashboard metrics retrieved", dashboard));
    }

    @PostMapping("/accounts/{id}/freeze")
    public ResponseEntity<ApiResponse<String>> freezeAccount(@PathVariable Long id) {
        adminService.freezeAccount(id);
        return ResponseEntity.ok(ApiResponse.success("Account frozen successfully", null));
    }

    @PostMapping("/accounts/{id}/unfreeze")
    public ResponseEntity<ApiResponse<String>> unfreezeAccount(@PathVariable Long id) {
        adminService.unfreezeAccount(id);
        return ResponseEntity.ok(ApiResponse.success("Account unfrozen successfully", null));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Object>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success("Users retrieved", adminService.getAllUsers()));
    }

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getAllTransactions(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String accountNumber,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            Pageable pageable) {
        
        LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate + "T00:00:00") : null;
        LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate + "T23:59:59") : null;
        TransactionType transactionType = type != null ? TransactionType.valueOf(type) : null;
        TransactionStatus transactionStatus = status != null ? TransactionStatus.valueOf(status) : null;
        
        Page<TransactionResponse> transactions = adminService.getAllTransactions(
                start, end, transactionType, transactionStatus, username, accountNumber, minAmount, maxAmount, pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved", transactions));
    }
}