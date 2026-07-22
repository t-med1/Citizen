package com.company.platform.service.implementations;

import com.company.platform.dto.NotificationDTO;
import com.company.platform.entity.Notification;
import com.company.platform.entity.User;
import com.company.platform.exception.ResourceNotFoundException;
import com.company.platform.mapper.NotificationMapper;
import com.company.platform.repository.NotificationRepository;
import com.company.platform.service.interfaces.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public List<NotificationDTO> findForUser(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public long countUnread(User user) {
        return notificationRepository.countByUserAndReadFalse(user);
    }

    @Override
    public void notify(User user, String message, String link) {
        if (user == null) {
            return;
        }
        Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .link(link)
                .read(false)
                .build();
        notificationRepository.save(notification);
    }

    @Override
    public void markAsRead(Long id, User currentUser) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", id));
        if (!notification.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Cette notification ne vous appartient pas");
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead(User user) {
        List<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);
        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
    }
}
