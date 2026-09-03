package com.superbuilt.mini.document;

import com.superbuilt.mini.ai.EmbeddingService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentSearchService {

    private final DocumentSearchRepository searchRepository;
    private final EmbeddingService embeddingService;

    public DocumentSearchService(
            DocumentSearchRepository searchRepository,
            EmbeddingService embeddingService
    ) {
        this.searchRepository = searchRepository;
        this.embeddingService = embeddingService;
    }

    public List<SearchResult> search(
            Long projectId,
            String query,
            int limit
    ) {

        float[] queryEmbedding =
                embeddingService.embed(query);

        String vector =
                toPgVector(queryEmbedding);

        List<DocumentChunk> chunks =
                searchRepository.searchSimilarChunks(
                        projectId,
                        vector,
                        limit
                );

        return chunks.stream()
                .map(chunk ->
                        new SearchResult(
                                chunk.getId(),
                                chunk.getDocument().getId(),
                                chunk.getDocument().getName(),
                                chunk.getChunkIndex(),
                                chunk.getContent()
                        )
                )
                .toList();
    }

    private String toPgVector(float[] embedding) {

        StringBuilder builder = new StringBuilder("[");

        for (int i = 0; i < embedding.length; i++) {

            if (i > 0) {
                builder.append(",");
            }

            builder.append(embedding[i]);
        }

        builder.append("]");

        return builder.toString();
    }
}