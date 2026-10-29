package com.securebank.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponse {
    private Long totalUsers;
    private Long totalAccounts;
    private Long newUsersThisWeek;
    private BigDecimal todayTransactionVolume;
    private Long todayTransactionCount;
    
    // Account status distribution
    private Long activeAccounts;
    private Long frozenAccounts;
    private Long closedAccounts;
    
    // Transaction type distribution for today
    private BigDecimal todayDeposits;
    private BigDecimal todayWithdrawals;
    private BigDecimal todayTransfers;
    
    // Transaction counts by type
    private Long depositCount;
    private Long withdrawalCount;
    private Long transferCount;
    
    // Daily transaction volume for charts (last 7 days)
    private Map<String, BigDecimal> dailyVolume;
}