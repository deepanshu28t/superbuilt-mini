package com.superbuilt.mini.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.superbuilt.mini.document.DocumentSearchService;
import com.superbuilt.mini.document.SearchResult;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class IssueDetectionService {

    private final DocumentSearchService documentSearchService;
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public IssueDetectionService(
            DocumentSearchService documentSearchService,
            ChatClient.Builder chatClientBuilder,
            ObjectMapper objectMapper
    ) {
        this.documentSearchService = documentSearchService;
        this.chatClient = chatClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public IssueDetectionResult analyze(
            Long projectId,
            String question
    ) {

        List<SearchResult> results =
                documentSearchService.search(
                        projectId,
                        question,
                        10
                );

        String context = buildContext(results);

        String prompt = """
                You are an autonomous construction project coordination
                assistant.

                Analyze the provided project document evidence and determine
                whether there is a coordination issue, conflict, restriction,
                compliance concern, or other actionable problem.

                IMPORTANT RULES:

                1. Use ONLY the provided document evidence.
                2. Do not invent facts.
                3. If there is insufficient evidence, issueDetected must be false.
                4. If two documents contain conflicting or dependent
                   requirements, identify the coordination issue.
                5. confidenceScore must be between 0 and 1.
                6. riskScore must be between 0 and 1.
                7. issueType must be one of:
                   CLASH, COMPLIANCE, RFI, DELAY, COORDINATION, OTHER.
                8. severity must be one of:
                   LOW, MEDIUM, HIGH, CRITICAL.
                9. Return ONLY valid JSON.
                10. Do not wrap the JSON in markdown code fences.

                PROJECT DOCUMENT EVIDENCE:

                %s

                Analyze the evidence and return exactly this JSON structure:

                {
                  "issueDetected": true,
                  "title": "...",
                  "description": "...",
                  "issueType": "CLASH",
                  "severity": "HIGH",
                  "requiresDecision": true,
                  "confidenceScore": 0.0,
                  "riskScore": 0.0,
                  "recommendedAction": "..."
                }

                ANALYSIS REQUEST:
                %s
                """.formatted(context, question);

        String response = chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();

        return parseResult(response);
    }

    private String buildContext(
            List<SearchResult> results
    ) {

        StringBuilder context = new StringBuilder();

        for (SearchResult result : results) {

            context.append("""
                    
                    DOCUMENT: %s
                    CHUNK: %d

                    CONTENT:
                    %s

                    ------------------------------
                    """.formatted(
                    result.documentName(),
                    result.chunkIndex(),
                    result.content()
            ));
        }

        return context.toString();
    }

    private IssueDetectionResult parseResult(
            String response
    ) {

        try {

            String cleanedResponse =
                    cleanJsonResponse(response);

            return objectMapper.readValue(
                    cleanedResponse,
                    IssueDetectionResult.class
            );

        } catch (JsonProcessingException e) {

            throw new IllegalStateException(
                    "Failed to parse AI issue detection response: "
                            + response,
                    e
            );
        }
    }

    private String cleanJsonResponse(String response) {

        String cleaned = response.trim();

        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }

        if (cleaned.endsWith("```")) {
            cleaned =
                    cleaned.substring(
                            0,
                            cleaned.length() - 3
                    );
        }

        return cleaned.trim();
    }
}