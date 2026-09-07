package com.superbuilt.mini.issue;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class IssueEmbeddingVectorRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void     saveEmbedding(
            Long issueId,
            String vector
    ) {

        entityManager.createNativeQuery("""
                INSERT INTO issue_embeddings (
                    issue_id,
                    embedding
                )
                VALUES (
                    :issueId,
                    CAST(:vector AS vector)
                )
                ON CONFLICT (issue_id)
                DO UPDATE SET embedding = EXCLUDED.embedding
                """)
                .setParameter("issueId", issueId)
                .setParameter("vector", vector)
                .executeUpdate();
    }

    @SuppressWarnings("unchecked")
    public List<IssueSimilarityResult> findSimilarIssues(
            Long projectId,
            String vector,
            int limit
    ) {

        List<Object[]> results =
                entityManager.createNativeQuery("""
                        SELECT
                            i.id,
                            i.title,
                            i.description,
                            1 - (
                                ie.embedding <=> CAST(:vector AS vector)
                            ) AS similarity
                        FROM issues i
                        JOIN issue_embeddings ie
                            ON i.id = ie.issue_id
                        WHERE i.project_id = :projectId
                          AND ie.embedding IS NOT NULL
                        ORDER BY ie.embedding <=> CAST(:vector AS vector)
                        LIMIT :limit
                        """)
                        .setParameter("projectId", projectId)
                        .setParameter("vector", vector)
                        .setParameter("limit", limit)
                        .getResultList();

        return results.stream()
                .map(row -> new IssueSimilarityResult(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        (String) row[2],
                        ((Number) row[3]).doubleValue()
                ))
                .toList();
    }
}