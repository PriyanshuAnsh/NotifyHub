package com.notifyhub.notifyhub.notification.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateNotificationRequest(

        @NotBlank(message = "toEmail is required")
        @Email(message = "toEmail must be a valid email")
        @Size(max = 255)
        String toEmail,

        @NotBlank(message = "subject is required")
        @Size(max = 255)
        String subject,

        @NotBlank(message = "body is required")
        String body,

        // Optional branding — any present opts into the rich HTML template.
        @Size(max = 1024) String logoUrl,
        @Size(max = 1024) String bannerUrl,
        @Size(max = 255) String heading,
        @Size(max = 150) String ctaText,
        @Size(max = 1024) String ctaUrl) {
}
