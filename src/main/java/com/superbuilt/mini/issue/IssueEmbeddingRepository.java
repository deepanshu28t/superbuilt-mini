package com.superbuilt.mini.issue;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IssueEmbeddingRepository
        extends JpaRepository<IssueEmbedding, Long> {

    Optional<IssueEmbedding> findByIssueId(Long issueId);
}