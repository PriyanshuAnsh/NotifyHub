package com.notifyhub.notifyhub.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A notification intent persisted as the source of truth for its delivery lifecycle.
 */
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "to_email", nullable = false, length = 255)
    private String toEmail;

    @Column(name = "subject", nullable = false, length = 255)
    private String subject;

    @Column(name = "body", nullable = false, columnDefinition = "text")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private NotificationStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "logo_url", length = 1024)
    private String logoUrl;

    @Column(name = "banner_url", length = 1024)
    private String bannerUrl;

    @Column(name = "heading", length = 255)
    private String heading;

    @Column(name = "cta_text", length = 150)
    private String ctaText;

    @Column(name = "cta_url", length = 1024)
    private String ctaUrl;

    protected Notification() {
        // for JPA
    }

    public static Notification create(String toEmail, String subject, String body) {
        return create(toEmail, subject, body, Branding.NONE);
    }

    public static Notification create(String toEmail, String subject, String body, Branding branding) {
        Notification n = new Notification();
        n.id = UUID.randomUUID();
        n.toEmail = toEmail;
        n.subject = subject;
        n.body = body;
        n.status = NotificationStatus.PENDING;
        n.createdAt = LocalDateTime.now();
        Branding b = branding == null ? Branding.NONE : branding;
        n.logoUrl = b.logoUrl();
        n.bannerUrl = b.bannerUrl();
        n.heading = b.heading();
        n.ctaText = b.ctaText();
        n.ctaUrl = b.ctaUrl();
        return n;
    }

    public Branding getBranding() {
        return new Branding(logoUrl, bannerUrl, heading, ctaText, ctaUrl);
    }

    public void markSent() {
        this.status = NotificationStatus.SENT;
        this.updatedAt = LocalDateTime.now();
    }

    public void markFailed() {
        this.status = NotificationStatus.FAILED;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getToEmail() {
        return toEmail;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
