package com.superbuilt.mini.issue;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/issues")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Issue createIssue(
            @PathVariable Long projectId,
            @RequestBody Issue issue
    ) {
        return issueService.createIssue(projectId, issue);
    }

    @PatchMapping("/{issueId}/status")
    public Issue updateIssueStatus(
            @PathVariable Long issueId,
            @RequestBody UpdateIssueStatusRequest request
    ) {
       System.out.println("issue status");
        return issueService.updateIssueStatus(
                issueId,
                request.status()
        );
    }

    @GetMapping
    public List<Issue> getIssues(
            @PathVariable Long projectId
    ) {
        return issueService.getIssuesByProject(projectId);
    }

    @GetMapping("/open")
    public List<Issue> getOpenIssues(
            @PathVariable Long projectId
    ) {
        return issueService.getOpenIssues(projectId);
    }

    @GetMapping("/decision-required")
    public List<Issue> getDecisionRequiredIssues(
            @PathVariable Long projectId
    ) {
        return issueService.getDecisionRequiredIssues(projectId);
    }

    @GetMapping("/{issueId}")
    public Issue getIssue(
            @PathVariable Long issueId
    ) {
        return issueService.getIssueById(issueId);
    }
}