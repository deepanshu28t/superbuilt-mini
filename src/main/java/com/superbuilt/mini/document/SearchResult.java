package com.superbuilt.mini.document;

public record SearchResult(
        Long chunkId,
        Long documentId,
        String documentName,
        Integer chunkIndex,
        String content
) {
}