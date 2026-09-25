package com.example.online.banking.service;

import com.example.online.banking.ENum.NotificationStatus;
import com.example.online.banking.ENum.NotificationType;
import com.example.online.banking.model.Notification;
import com.example.online.banking.model.User;
import com.example.online.banking.repo.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {



    private final NotificationRepository notificationRepository;



    @Transactional
    public Notification createNotification(
            User user,
            String title,
            String message,
            NotificationType type) {

        Notification notification = new Notification();

        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setNotificationType(type);
        notification.setStatus(NotificationStatus.UNREAD);
        notification.setCreatedAt(LocalDateTime.now());

        Notification saved =
                notificationRepository.save(notification);

        log.info(
                "Notification created for user: {}, type: {}",
                user.getUsername(),
                type
        );

        return saved;
    }

    @Transactional(readOnly = true)
    public List<Notification> getUserNotifications(
            Long userId) {

        return notificationRepository
                .findByUserUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public Notification markAsRead(Long notificationId) {

        Notification notification =
                notificationRepository.findById(notificationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notification not found"
                                )
                        );

        notification.setStatus(NotificationStatus.READ);
        notification.setReadAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }
}