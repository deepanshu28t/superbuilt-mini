package com.superbuilt.mini.notification;

import java.time.LocalDateTime;

public record MailLogResponse(
        Long id,
        String recipient,
        String subject,
        MailLogType type,
        MailLogStatus status,
        String errorMessage,
        LocalDateTime sentAt
) {
    public static MailLogResponse from(MailLog log) {
        return new MailLogResponse(
                log.getId(),
                log.getRecipient(),
                log.getSubject(),
                log.getType(),
                log.getStatus(),
                log.getErrorMessage(),
                log.getSentAt()
        );
    }
}