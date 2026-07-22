package com.company.platform.controller;

import com.company.platform.entity.User;
import com.company.platform.service.interfaces.NotificationService;
import com.company.platform.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final SecurityUtil securityUtil;

    @GetMapping("/notifications")
    public String list(Model model) {
        User currentUser = securityUtil.getCurrentUser();
        model.addAttribute("notifications", notificationService.findForUser(currentUser));
        return "notifications/list";
    }

    @PostMapping("/notifications/{id}/read")
    public String markAsRead(@PathVariable Long id) {
        User currentUser = securityUtil.getCurrentUser();
        notificationService.markAsRead(id, currentUser);
        return "redirect:/notifications";
    }

    @PostMapping("/notifications/read-all")
    public String markAllAsRead() {
        User currentUser = securityUtil.getCurrentUser();
        notificationService.markAllAsRead(currentUser);
        return "redirect:/notifications";
    }
}
