package com.superbuilt.mini.issue;

import com.superbuilt.mini.ai.EmbeddingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IssueEmbeddingService {

    private final EmbeddingService embeddingService;

    private final IssueEmbeddingVectorRepository
            issueEmbeddingVectorRepository;

    private final IssueRepository issueRepository;

    public IssueEmbeddingService(
            EmbeddingService embeddingService,
            IssueEmbeddingVectorRepository issueEmbeddingVectorRepository,
            IssueRepository issueRepository
    ) {
        this.embeddingService = embeddingService;
        this.issueEmbeddingVectorRepository =
                issueEmbeddingVectorRepository;
        this.issueRepository = issueRepository;
    }

    @Transactional
    public void createEmbedding(Issue issue) {

        String textToEmbed =
                buildIssueText(issue);

        float[] embedding =
                embeddingService.embed(textToEmbed);

        String vector =
                toPgVector(embedding);

        issueEmbeddingVectorRepository.saveEmbedding(
                issue.getId(),
                vector
        );
    }
    @Transactional
    public void createEmbeddingForIssue(
            Long issueId
    ) {

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Issue not found with id: "
                                        + issueId
                        )
                );

        createEmbedding(issue);
    }

    public List<IssueSimilarityResult> findSimilarIssues(
            Long projectId,
            String title,
            String description,
            int limit
    ) {

        String textToEmbed =
                title + "\n" + description;

        float[] embedding =
                embeddingService.embed(textToEmbed);

        String vector =
                toPgVector(embedding);

        return issueEmbeddingVectorRepository
                .findSimilarIssues(
                        projectId,
                        vector,
                        limit
                );
    }

    private String buildIssueText(Issue issue) {

        return issue.getTitle()
                + "\n"
                + issue.getDescription();
    }

    private String toPgVector(float[] embedding) {

        StringBuilder builder =
                new StringBuilder("[");

        for (int i = 0;
             i < embedding.length;
             i++) {

            if (i > 0) {
                builder.append(",");
            }

            builder.append(embedding[i]);
        }

        builder.append("]");

        return builder.toString();
    }
}