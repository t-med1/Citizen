package com.company.platform.controller;

import com.company.platform.entity.User;
import com.company.platform.service.interfaces.NotificationService;
import com.company.platform.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributesAdvice {

    private final SecurityUtil securityUtil;
    private final NotificationService notificationService;

    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        User currentUser = securityUtil.getCurrentUser();
        if (currentUser != null) {
            model.addAttribute("unreadNotifCount", notificationService.countUnread(currentUser));
        }
    }
}
