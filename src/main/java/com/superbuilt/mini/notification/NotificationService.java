package com.superbuilt.mini.notification;

import com.superbuilt.mini.action.Action;
import com.superbuilt.mini.issue.Issue;
import com.superbuilt.mini.issue.IssueSeverity;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final EmailNotificationService emailNotificationService;

    public NotificationService(
            EmailNotificationService emailNotificationService
    ) {
        this.emailNotificationService =
                emailNotificationService;
    }

    public void handleIssueNotification(
            Issue issue,
            Action action
    ) {

        System.out.println(
                "Notification check for Issue ID: "
                        + issue.getId()
                        + " | Severity: "
                        + issue.getSeverity()
        );

        if (issue.getSeverity() == IssueSeverity.HIGH) {

            System.out.println(
                    "HIGH severity issue detected. Sending email..."
            );

            emailNotificationService
                    .sendHighSeverityIssueNotification(
                            issue,
                            action
                    );

            System.out.println(
                    "Email notification sent successfully!"
            );

        } else {

            System.out.println(
                    "No email notification required."
            );
        }
    }
}