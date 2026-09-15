package com.superbuilt.mini.notification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MailLogRepository
        extends JpaRepository<MailLog, Long> {

    List<MailLog> findTop100ByProjectIdOrderBySentAtDesc(
            Long projectId
    );

    long countByProjectId(Long projectId);

    long countByProjectIdAndStatus(
            Long projectId,
            MailLogStatus status
    );

    long countByProjectIdAndTypeAndStatus(
            Long projectId,
            MailLogType type,
            MailLogStatus status
    );
}