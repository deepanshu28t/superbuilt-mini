package com.superbuilt.mini.issue;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/issues")
public class IssueEmbeddingController {

    private final IssueEmbeddingService issueEmbeddingService;

    public IssueEmbeddingController(
            IssueEmbeddingService issueEmbeddingService
    ) {
        this.issueEmbeddingService = issueEmbeddingService;
    }

    @PostMapping("/{issueId}/embedding")
    public String createEmbedding(
            @PathVariable Long issueId
    ) {

        issueEmbeddingService
                .createEmbeddingForIssue(issueId);

        return "Embedding created successfully";
    }
}