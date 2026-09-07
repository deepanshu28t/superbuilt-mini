package com.superbuilt.mini.ai;

public record IssueDetectionResult(
        boolean issueDetected,
        String title,
        String description,
        String issueType,
        String severity,
        boolean requiresDecision,
        double confidenceScore,
        double riskScore,
        String recommendedAction
) {
}