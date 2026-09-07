package com.superbuilt.mini.issue;

public record IssueSimilarityResult(
        Long issueId,
        String title,
        String description,
        double similarity
) {
}