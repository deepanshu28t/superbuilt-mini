package com.superbuilt.mini.notification;

import com.superbuilt.mini.action.Action;
import com.superbuilt.mini.issue.Issue;
import com.superbuilt.mini.project.Project;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailNotificationService(
            JavaMailSender mailSender
    ) {
        this.mailSender = mailSender;
    }

    public void sendHighSeverityIssueNotification(
            Issue issue,
            Action action
    ) {

        Project project = issue.getProject();

        SimpleMailMessage message =
                new SimpleMailMessage();
        message.setFrom(fromEmail);

        // Temporary: send notification to configured email
        message.setTo(fromEmail);

        message.setSubject(
                "HIGH Priority Issue Detected - "
                        + project.getName()
        );

        String emailBody = """
                A HIGH priority project issue has been detected.

                PROJECT
                -------
                %s

                ISSUE
                -----
                %s

                DESCRIPTION
                -----------
                %s

                SEVERITY
                --------
                %s

                RECOMMENDED ACTION
                ------------------
                %s

                Please review and take the necessary action.

                ---------------------------------
                SuperBuilt Mini
                AI Project Coordination Agent
                """.formatted(
                project.getName(),
                issue.getTitle(),
                issue.getDescription(),
                issue.getSeverity(),
                action.getDescription()
        );

        message.setText(emailBody);

        mailSender.send(message);
    }
}