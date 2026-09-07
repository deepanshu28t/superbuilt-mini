package com.superbuilt.mini.ai;

import com.superbuilt.mini.issue.Issue;
import com.superbuilt.mini.issue.IssueEmbeddingService;
import com.superbuilt.mini.issue.IssueRepository;
import com.superbuilt.mini.issue.IssueSimilarityResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IssueDuplicateService {

    private static final double DUPLICATE_THRESHOLD = 0.90;

    private final IssueRepository issueRepository;
    private final IssueEmbeddingService issueEmbeddingService;

    public IssueDuplicateService(
            IssueRepository issueRepository,
            IssueEmbeddingService issueEmbeddingService
    ) {
        this.issueRepository = issueRepository;
        this.issueEmbeddingService = issueEmbeddingService;
    }

    public Issue findDuplicate(
            Long projectId,
            String title,
            String description
    ) {

        System.out.println("\n======================================");
        System.out.println("DUPLICATE DETECTION STARTED");
        System.out.println("Project ID: " + projectId);
        System.out.println("New Title: " + title);
        System.out.println("======================================");

        // --------------------------------------------
        // LAYER 1: Exact normalized title match
        // --------------------------------------------

        String normalizedNewTitle = normalize(title);

        List<Issue> existingIssues =
                issueRepository.findByProjectId(projectId);

        System.out.println(
                "Existing issues found: " + existingIssues.size()
        );

        Issue exactDuplicate = existingIssues.stream()
                .filter(issue ->
                        normalize(issue.getTitle())
                                .equalsIgnoreCase(normalizedNewTitle)
                )
                .findFirst()
                .orElse(null);

        if (exactDuplicate != null) {

            System.out.println(
                    "DUPLICATE FOUND BY EXACT MATCH!"
            );

            System.out.println(
                    "Existing Issue ID: "
                            + exactDuplicate.getId()
            );

            return exactDuplicate;
        }

        // --------------------------------------------
        // LAYER 2: Semantic similarity search
        // --------------------------------------------

        System.out.println(
                "No exact duplicate found."
        );

        System.out.println(
                "Starting SEMANTIC similarity search..."
        );

        List<IssueSimilarityResult> similarIssues =
                issueEmbeddingService.findSimilarIssues(
                        projectId,
                        title,
                        description,
                        5
                );

        System.out.println(
                "Semantic results found: "
                        + similarIssues.size()
        );

        similarIssues.forEach(result ->
                System.out.println(
                        "Similar Issue ID: "
                                + result.issueId()
                                + " | Title: "
                                + result.title()
                                + " | Similarity: "
                                + result.similarity()
                )
        );

        IssueSimilarityResult semanticDuplicate =
                similarIssues.stream()
                        .filter(result ->
                                result.similarity()
                                        >= DUPLICATE_THRESHOLD
                        )
                        .findFirst()
                        .orElse(null);

        if (semanticDuplicate != null) {

            System.out.println(
                    "DUPLICATE FOUND BY SEMANTIC SEARCH!"
            );

            System.out.println(
                    "Matched Issue ID: "
                            + semanticDuplicate.issueId()
            );

            return issueRepository
                    .findById(
                            semanticDuplicate.issueId()
                    )
                    .orElse(null);
        }

        System.out.println(
                "NO DUPLICATE FOUND."
        );

        System.out.println(
                "======================================\n"
        );

        return null;
    }

    private String normalize(String value) {

        if (value == null) {
            return "";
        }

        return value
                .trim()
                .replaceAll("\\s+", " ");
    }
}