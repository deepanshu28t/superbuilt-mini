package com.superbuilt.mini.issue;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {

    List<Issue> findByProjectId(Long projectId);

    List<Issue> findByProjectIdAndStatus(
            Long projectId,
            IssueStatus status
    );

    List<Issue> findByProjectIdAndRequiresDecisionTrue(
            Long projectId
    );
}