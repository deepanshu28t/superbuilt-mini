package com.superbuilt.mini.ai;

public record AgentExecutionResult(

        boolean issueDetected,

        boolean issueCreated,

        boolean duplicate,

        Long issueId,

        Long actionId,

        String message,

        IssueDetectionResult detection

) {
}