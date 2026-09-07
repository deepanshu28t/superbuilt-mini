package com.superbuilt.mini.ai;

import com.superbuilt.mini.action.Action;
import com.superbuilt.mini.action.ActionPriority;
import com.superbuilt.mini.action.ActionService;
import com.superbuilt.mini.action.ActionStatus;
import com.superbuilt.mini.issue.Issue;
import com.superbuilt.mini.issue.IssueService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CoordinationAgentService {

    private final IssueDetectionService issueDetectionService;
    private final IssueDetectionValidator validator;
    private final IssueService issueService;
    private final ActionService actionService;
    private final IssueDuplicateService issueDuplicateService;

    public CoordinationAgentService(
            IssueDetectionService issueDetectionService,
            IssueDetectionValidator validator,
            IssueService issueService,
            ActionService actionService,
            IssueDuplicateService issueDuplicateService
    ) {
        this.issueDetectionService = issueDetectionService;
        this.validator = validator;
        this.issueService = issueService;
        this.actionService = actionService;
        this.issueDuplicateService = issueDuplicateService;
    }

    @Transactional
    public AgentExecutionResult detectAndCreate(
            Long projectId,
            String question
    ) {

        // 1. Analyze project evidence
        IssueDetectionResult detection =
                issueDetectionService.analyze(
                        projectId,
                        question
                );

        // 2. Validate AI output
        validator.validate(detection);

        // 3. No issue found
        if (!detection.issueDetected()) {

            return new AgentExecutionResult(
                    false,
                    false,
                    false,
                    null,
                    null,
                    "No coordination issue detected.",
                    detection
            );
        }

        // 4. Check for duplicate
        var existingIssue =
                issueDuplicateService.findDuplicate(
                        projectId,
                        detection.title(),
                        detection.description()
                );

        if (existingIssue != null) {

            return new AgentExecutionResult(
                    true,
                    false,
                    true,
                    existingIssue.getId(),
                    null,
                    "A similar issue already exists.",
                    detection
            );
        }

        // 5. Create Issue
        Issue issue =
                issueService.createIssueFromAiDetection(
                        projectId,
                        detection
                );

        // 6. Create Action
        Action action = Action.builder()
                .title(detection.recommendedAction())
                .description(
                        "Action generated from AI-detected issue: "
                                + issue.getTitle()
                )
                .status(ActionStatus.OPEN)
                .priority(
                        mapPriority(detection.severity())
                )
                .build();

        action =
                actionService.createAction(
                        issue.getId(),
                        action
                );

        // 7. Return result
        return new AgentExecutionResult(
                true,
                true,
                false,
                issue.getId(),
                action.getId(),
                "New coordination issue detected and created.",
                detection
        );
    }
    private ActionPriority mapPriority(
            String severity
    ) {

        return switch (severity) {
            case "CRITICAL" ->
                    ActionPriority.CRITICAL;

            case "HIGH" ->
                    ActionPriority.HIGH;

            case "MEDIUM" ->
                    ActionPriority.MEDIUM;

            case "LOW" ->
                    ActionPriority.LOW;

            default ->
                    throw new IllegalArgumentException(
                            "Unknown severity: " + severity
                    );
        };
    }
}