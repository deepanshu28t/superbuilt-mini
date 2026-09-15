package com.superbuilt.mini.notification;

import com.superbuilt.mini.action.Action;
import com.superbuilt.mini.issue.Issue;
import com.superbuilt.mini.project.Project;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class EmailNotificationService {

    private final JavaMailSender mailSender;
    private final MailLogRepository mailLogRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailNotificationService(
            JavaMailSender mailSender,
            MailLogRepository mailLogRepository
    ) {
        this.mailSender = mailSender;
        this.mailLogRepository = mailLogRepository;
    }

    public boolean sendHighSeverityIssueNotification(
            Issue issue,
            Action action
    ) {
        Project project = issue.getProject();

        String subject =
                "High Priority Issue Detected - "
                        + project.getName();

        String body = """
                A high-priority project issue has been detected.

                PROJECT: %s

                ISSUE: %s

                DESCRIPTION:
                %s

                SEVERITY: %s

                RECOMMENDED ACTION:
                %s
                """.formatted(
                project.getName(),
                issue.getTitle(),
                issue.getDescription(),
                issue.getSeverity(),
                action.getDescription()
        );

        return sendAndLog(
                project.getId(),
                fromEmail,
                subject,
                body,
                MailLogType.ISSUE_ALERT
        );
    }

    public boolean sendMorningBrief(
            Long projectId,
            String recipient,
            String subject,
            String emailBody
    ) {
        return sendAndLog(
                projectId,
                recipient,
                subject,
                emailBody,
                MailLogType.MORNING_BRIEF
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected boolean sendAndLog(
            Long projectId,
            String recipient,
            String subject,
            String emailBody,
            MailLogType type
    ) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(fromEmail);
            message.setTo(recipient);
            message.setSubject(subject);
            message.setText(emailBody);

            mailSender.send(message);

            saveLog(
                    projectId,
                    recipient,
                    subject,
                    type,
                    MailLogStatus.SENT,
                    null
            );

            return true;
        } catch (RuntimeException exception) {
            saveLog(
                    projectId,
                    recipient,
                    subject,
                    type,
                    MailLogStatus.FAILED,
                    exception.getMessage()
            );

            return false;
        }
    }

    private void saveLog(
            Long projectId,
            String recipient,
            String subject,
            MailLogType type,
            MailLogStatus status,
            String errorMessage
    ) {
        mailLogRepository.save(
                MailLog.builder()
                        .projectId(projectId)
                        .recipient(recipient)
                        .subject(subject)
                        .type(type)
                        .status(status)
                        .errorMessage(errorMessage)
                        .sentAt(LocalDateTime.now())
                        .build()
        );
    }
}