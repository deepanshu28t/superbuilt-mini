package com.superbuilt.mini.ai;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects/{projectId}/ai")
public class IssueDetectionController {

    private final IssueDetectionService issueDetectionService;
    private final IssueDetectionValidator validator;
    private final CoordinationAgentService coordinationAgentService;

    public IssueDetectionController(
            IssueDetectionService issueDetectionService,
            IssueDetectionValidator validator,
            CoordinationAgentService coordinationAgentService
    ) {
        this.issueDetectionService = issueDetectionService;
        this.validator = validator;
        this.coordinationAgentService=coordinationAgentService;
    }

    @PostMapping("/detect-issues")
    public IssueDetectionResult detectIssues(
            @PathVariable Long projectId,
            @RequestParam String question
    ) {

        IssueDetectionResult result =
                issueDetectionService.analyze(
                        projectId,
                        question
                );

        validator.validate(result);

        return result;
    }
    @PostMapping("/detect-and-create")
    public AgentExecutionResult detectAndCreate(
            @PathVariable Long projectId,
            @RequestParam String question
    ) {

        return coordinationAgentService.detectAndCreate(
                projectId,
                question
        );
    }
}