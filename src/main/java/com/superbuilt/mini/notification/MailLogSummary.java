package com.superbuilt.mini.notification;

public record MailLogSummary(
        long totalEmails,
        long emailsSent,
        long failedEmails,
        long morningBriefsSent
) {
}