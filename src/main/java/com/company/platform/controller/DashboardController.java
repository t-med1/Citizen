package com.company.platform.controller;

import com.company.platform.entity.User;
import com.company.platform.service.interfaces.ComplaintService;
import com.company.platform.service.interfaces.DashboardService;
import com.company.platform.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final ComplaintService complaintService;
    private final SecurityUtil securityUtil;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User currentUser = securityUtil.getCurrentUser();
        model.addAttribute("currentUser", currentUser);

        if (currentUser.hasRole("ROLE_ADMIN")) {
            model.addAttribute("stats", dashboardService.getStats());
            return "dashboard-admin";
        } else if (currentUser.hasRole("ROLE_AGENT")) {
            model.addAttribute("myComplaints", complaintService.findForUser(currentUser));
            return "dashboard-agent";
        } else {
            model.addAttribute("myComplaints", complaintService.findForUser(currentUser));
            return "dashboard-citizen";
        }
    }
}
