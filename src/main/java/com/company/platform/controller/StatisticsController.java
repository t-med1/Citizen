package com.company.platform.controller;

import com.company.platform.service.interfaces.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class StatisticsController {

    private final DashboardService dashboardService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/statistics")
    public String statistics(Model model) {
        model.addAttribute("stats", dashboardService.getStats());
        return "statistics/statistics";
    }
}
