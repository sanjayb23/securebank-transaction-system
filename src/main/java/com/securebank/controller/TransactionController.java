package com.securebank.controller;

import com.securebank.dto.request.DepositRequest;
import com.securebank.dto.request.TransferRequest;
import com.securebank.dto.request.WithdrawRequest;
import com.securebank.dto.response.ApiResponse;
import com.securebank.dto.response.TransactionResponse;
import com.securebank.entity.User;
import com.securebank.enums.TransactionStatus;
import com.securebank.enums.TransactionType;
import com.securebank.repository.UserRepository;
import com.securebank.service.TransactionService;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final UserRepository userRepository;

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<TransactionResponse>> deposit(@RequestBody DepositRequest request) {
        Long userId = getCurrentUserId();
        TransactionResponse response = transactionService.deposit(request, userId);
        return ResponseEntity.ok(ApiResponse.success("Deposit successful", response));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<TransactionResponse>> withdraw(@RequestBody WithdrawRequest request) {
        Long userId = getCurrentUserId();
        TransactionResponse response = transactionService.withdraw(request, userId);
        return ResponseEntity.ok(ApiResponse.success("Withdrawal successful", response));
    }

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<TransactionResponse>> transfer(@RequestBody TransferRequest request) {
        Long userId = getCurrentUserId();
        TransactionResponse response = transactionService.transfer(request, userId);
        return ResponseEntity.ok(ApiResponse.success("Transfer successful", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getTransactions(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            Pageable pageable) {
        
        Long userId = getCurrentUserId();
        
        LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate + "T00:00:00") : null;
        LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate + "T23:59:59") : null;
        TransactionType transactionType = type != null ? TransactionType.valueOf(type) : null;
        TransactionStatus transactionStatus = status != null ? TransactionStatus.valueOf(status) : null;
        
        Page<TransactionResponse> page = transactionService.getTransactionsWithFilters(
                userId, start, end, transactionType, transactionStatus, pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved", page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransaction(@PathVariable String id) {
        Long userId = getCurrentUserId();
        TransactionResponse response = transactionService.getTransactionById(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Transaction details retrieved", response));
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }
}