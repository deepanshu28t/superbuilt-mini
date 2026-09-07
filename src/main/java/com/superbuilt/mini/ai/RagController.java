package com.superbuilt.mini.ai;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/projects/{projectId}/rag")
public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @GetMapping
    public Map<String, Object> ask(
            @PathVariable Long projectId,
            @RequestParam String question
    ) {

        RagResponse response =
                ragService.answer(
                        projectId,
                        question
                );

        return Map.of(
                "projectId", projectId,
                "question", question,
                "answer", response.answer(),
                "sources", response.sources()
        );
    }
}