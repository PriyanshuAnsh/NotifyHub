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
        String body) {
}
