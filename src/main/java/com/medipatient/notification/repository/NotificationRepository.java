package com.medipatient.notification.repository;

import com.medipatient.notification.model.Notification;
import com.medipatient.notification.model.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Récupérer toutes les notifications d'un utilisateur (les plus récentes d'abord)
    List<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId);

    // Compter les notifications non lues (pour afficher le petit badge rouge sur la cloche)
    long countByUserIdAndStatus(UUID userId, NotificationStatus status);
}