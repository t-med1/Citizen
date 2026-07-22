package com.company.platform.service.interfaces;

import com.company.platform.dto.NotificationDTO;
import com.company.platform.entity.User;

import java.util.List;

public interface NotificationService {

    List<NotificationDTO> findForUser(User user);

    long countUnread(User user);

    void notify(User user, String message, String link);

    void markAsRead(Long id, User currentUser);

    void markAllAsRead(User user);
}
