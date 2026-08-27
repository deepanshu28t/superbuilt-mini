package com.superbuilt.mini.communication;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunicationRepository
        extends JpaRepository<Communication, Long> {

    List<Communication> findByProjectId(Long projectId);

    List<Communication> findByProjectIdAndStatus(
            Long projectId,
            CommunicationStatus status
    );
}