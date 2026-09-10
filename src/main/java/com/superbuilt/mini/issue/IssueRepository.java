package com.superbuilt.mini.issue;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {

    List<Issue> findByProjectId(Long projectId);

    List<Issue> findByProjectIdAndStatus(
            Long projectId,
            IssueStatus status
    );

    long countByProjectId(Long projectId);

    long countByProjectIdAndStatus(
            Long projectId,
            IssueStatus status
    );

    long countByProjectIdAndSeverity(
            Long projectId,
            IssueSeverity severity
    );

    long countByProjectIdAndRequiresDecisionTrue(Long projectId);

    List<Issue> findByProjectIdAndRequiresDecisionTrue(
            Long projectId
    );

    boolean existsByProjectIdAndTitleIgnoreCase(
            Long projectId,
            String title
    );
}