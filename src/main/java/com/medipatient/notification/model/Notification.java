package com.medipatient.notification.model;

import com.medipatient.profile.model.Profile;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Profile user; // Le destinataire

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status = NotificationStatus.UNREAD;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Optionnel : l'ID de la ressource liée (ex: ID du RDV)
    private Long relatedResourceId;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}