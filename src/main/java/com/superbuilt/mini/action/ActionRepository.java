package com.superbuilt.mini.action;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActionRepository extends JpaRepository<Action, Long> {

    List<Action> findByIssueId(Long issueId);

    List<Action> findByIssueProjectId(Long projectId);

    List<Action> findByIssueProjectIdAndStatus(
            Long projectId,
            ActionStatus status
    );

    List<Action> findByIssueProjectIdAndStatusNot(
            Long projectId,
            ActionStatus status
    );


}