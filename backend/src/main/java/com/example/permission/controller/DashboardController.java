package com.example.permission.controller;

import com.example.permission.dto.response.ApiResponse;
import com.example.permission.dto.response.StatsDTO;
import com.example.permission.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public ApiResponse<StatsDTO> getStats() {
        StatsDTO stats = dashboardService.getStats();
        return ApiResponse.success(stats);
    }
}