package com.superbuilt.mini.document;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/search")
public class DocumentSearchController {

    private final DocumentSearchService searchService;

    public DocumentSearchController(
            DocumentSearchService searchService
    ) {
        this.searchService = searchService;
    }

    @GetMapping
    public List<SearchResult> search(
            @PathVariable Long projectId,
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int limit
    ) {

        return searchService.search(
                projectId,
                query,
                limit
        );
    }
}