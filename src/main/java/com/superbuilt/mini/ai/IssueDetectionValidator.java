package com.superbuilt.mini.ai;

import com.superbuilt.mini.issue.IssueSeverity;
import com.superbuilt.mini.issue.IssueType;
import org.springframework.stereotype.Component;

@Component
public class IssueDetectionValidator {

    public void validate(
            IssueDetectionResult result
    ) {

        if (!result.issueDetected()) {
            return;
        }

        if (result.title() == null ||
                result.title().isBlank()) {

            throw new IllegalArgumentException(
                    "AI issue title cannot be empty"
            );
        }

        if (result.description() == null ||
                result.description().isBlank()) {

            throw new IllegalArgumentException(
                    "AI issue description cannot be empty"
            );
        }

        validateIssueType(
                result.issueType()
        );

        validateSeverity(
                result.severity()
        );

        validateScore(
                result.confidenceScore(),
                "confidenceScore"
        );

        validateScore(
                result.riskScore(),
                "riskScore"
        );
    }

    private void validateIssueType(
            String issueType
    ) {

        try {
            IssueType.valueOf(issueType);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Invalid issue type from AI: "
                            + issueType
            );
        }
    }

    private void validateSeverity(
            String severity
    ) {

        try {
            IssueSeverity.valueOf(severity);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Invalid severity from AI: "
                            + severity
            );
        }
    }

    private void validateScore(
            double score,
            String field
    ) {

        if (score < 0 || score > 1) {
            throw new IllegalArgumentException(
                    field + " must be between 0 and 1"
            );
        }
    }
}