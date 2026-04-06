package com.bank.controller;

import com.bank.dto.DashboardResponse;
import com.bank.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/{accountNumber}")
    public ResponseEntity<DashboardResponse> getDashboard(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(
                dashboardService.getDashboardSummary(accountNumber));
    }
}