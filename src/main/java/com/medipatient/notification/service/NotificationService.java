package com.medipatient.notification.service;

import com.medipatient.profile.model.Profile;
import com.medipatient.notification.model.Notification;
import com.medipatient.notification.model.NotificationStatus;
import com.medipatient.notification.model.NotificationType;
import com.medipatient.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * Crée et sauvegarde une nouvelle notification.
     * Sera appelé par AppointmentService ou MedicationService.
     */
    @Transactional
    public void createNotification(Profile user, String title, String message, NotificationType type, Long resourceId) {
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .relatedResourceId(resourceId)
                .status(NotificationStatus.UNREAD)
                .build();

        notificationRepository.save(notification);
    }

    /**
     * Récupère toutes les notifications d'un utilisateur.
     */
    public List<Notification> getNotificationsForUser(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Compte le nombre de notifications non lues (pour le badge sur l'UI).
     */
    public Long countUnreadNotifications(UUID userId) {
        return notificationRepository.countByUserIdAndStatus(userId, NotificationStatus.UNREAD);
    }

    /**
     * Marque une notification spécifique comme lue.
     */
    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setStatus(NotificationStatus.READ);
            notificationRepository.save(notification);
        });
    }

    /**
     * Marque TOUTES les notifications d'un utilisateur comme lues.
     */
    @Transactional
    public void markAllAsRead(UUID userId) {
        List<Notification> unread = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        unread.forEach(n -> {
            if (n.getStatus() == NotificationStatus.UNREAD) {
                n.setStatus(NotificationStatus.READ);
            }
        });
        notificationRepository.saveAll(unread);
    }

    /**
     * Supprime une notification.
     */
    @Transactional
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}