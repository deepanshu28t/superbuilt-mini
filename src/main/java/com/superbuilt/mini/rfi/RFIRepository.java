package com.superbuilt.mini.rfi;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RFIRepository extends JpaRepository<RFI, Long> {

    List<RFI> findByProjectId(Long projectId);

    List<RFI> findByProjectIdAndStatus(
            Long projectId,
            RFIStatus status
    );
}