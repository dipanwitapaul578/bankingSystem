package com.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class DashboardResponse {
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netBalance;
    private Map<String, BigDecimal> categoryTotals;
    private List<String> recentActivity;
    private Map<String, BigDecimal> monthlyTrends;
}