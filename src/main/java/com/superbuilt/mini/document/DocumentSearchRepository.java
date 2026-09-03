package com.superbuilt.mini.document;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentSearchRepository
        extends JpaRepository<DocumentChunk, Long> {

    @Query(value = """
            SELECT *
            FROM document_chunks
            WHERE document_id IN (
                SELECT id
                FROM documents
                WHERE project_id = :projectId
            )
            ORDER BY embedding <=> CAST(:embedding AS vector)
            LIMIT :limit
            """,
            nativeQuery = true)
    List<DocumentChunk> searchSimilarChunks(
            @Param("projectId") Long projectId,
            @Param("embedding") String embedding,
            @Param("limit") int limit
    );
}