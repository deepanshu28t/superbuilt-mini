package com.superbuilt.mini.notification;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/mail-logs")
public class MailLogController {

    private final MailLogRepository mailLogRepository;

    public MailLogController(
            MailLogRepository mailLogRepository
    ) {
        this.mailLogRepository = mailLogRepository;
    }

    @GetMapping
    public List<MailLogResponse> getMailLogs(
            @PathVariable Long projectId
    ) {
        return mailLogRepository
                .findTop100ByProjectIdOrderBySentAtDesc(projectId)
                .stream()
                .map(MailLogResponse::from)
                .toList();
    }

    @GetMapping("/summary")
    public MailLogSummary getMailSummary(
            @PathVariable Long projectId
    ) {
        return new MailLogSummary(
                mailLogRepository.countByProjectId(projectId),
                mailLogRepository.countByProjectIdAndStatus(
                        projectId,
                        MailLogStatus.SENT
                ),
                mailLogRepository.countByProjectIdAndStatus(
                        projectId,
                        MailLogStatus.FAILED
                ),
                mailLogRepository.countByProjectIdAndTypeAndStatus(
                        projectId,
                        MailLogType.MORNING_BRIEF,
                        MailLogStatus.SENT
                )
        );
    }
}