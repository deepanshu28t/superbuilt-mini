package com.superbuilt.mini.dashboard;

public record DashboardResponse(

        long totalDocuments,

        long processedDocuments,

        long totalIssues,

        long openIssues,

        long highSeverityIssues,

        long decisionRequiredIssues,

        long pendingActions

) {
}