package com.medipatient.notification.controller;

import com.medipatient.profile.model.Profile;
import com.medipatient.notification.model.Notification;
import com.medipatient.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // 1. Récupérer toutes les notifications de l'utilisateur connecté
    @GetMapping
    public ResponseEntity<List<Notification>> getMyNotifications(@AuthenticationPrincipal Profile currentUser) {
        return ResponseEntity.ok(notificationService.getNotificationsForUser(currentUser.getId()));
    }

    // 2. Récupérer le nombre de notifications non lues (pour le badge rouge)
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(@AuthenticationPrincipal Profile currentUser) {
        return ResponseEntity.ok(notificationService.countUnreadNotifications(currentUser.getId()));
    }

    // 3. Marquer une notification comme lue
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    // 4. Tout marquer comme lu
    @PatchMapping("/mark-all-read")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal Profile currentUser) {
        notificationService.markAllAsRead(currentUser.getId());
        return ResponseEntity.ok().build();
    }

    // 5. Supprimer une notification
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}