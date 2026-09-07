package com.superbuilt.mini.ai;

import com.superbuilt.mini.document.DocumentSearchService;
import com.superbuilt.mini.document.SearchResult;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import com.superbuilt.mini.ai.RagService;

import java.util.List;

@Service
public class RagService {

    private final DocumentSearchService documentSearchService;
    private final ChatClient chatClient;

    public RagService(
            DocumentSearchService documentSearchService,
            ChatClient.Builder chatClientBuilder
    ) {
        this.documentSearchService = documentSearchService;
        this.chatClient = chatClientBuilder.build();
    }

    public RagResponse answer(
            Long projectId,
            String question
    ) {

        List<SearchResult> results =
                documentSearchService.search(
                        projectId,
                        question,
                        5
                );

        String context = buildContext(results);

        String prompt = """
                You are an AI construction project coordination assistant.

                Answer the user's question using ONLY the provided project
                documents.

                If the documents do not contain enough information to answer
                the question, say that the information is not available in
                the provided project documents.

                Do not invent facts.

                PROJECT DOCUMENT CONTEXT:
                %s

                USER QUESTION:
                %s

                Provide a concise answer and mention which drawing/document
                supports your answer.
                """.formatted(context, question);

        String answer= chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();
        return new RagResponse(answer,results);
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
                    
                    """.formatted(
                    result.documentName(),
                    result.chunkIndex(),
                    result.content()
            ));
        }

        return context.toString();
    }
}