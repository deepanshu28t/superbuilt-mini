package com.superbuilt.mini.ai;

import com.superbuilt.mini.document.SearchResult;

import java.util.List;

public record RagResponse(
        String answer,
        List<SearchResult> sources
) {
}