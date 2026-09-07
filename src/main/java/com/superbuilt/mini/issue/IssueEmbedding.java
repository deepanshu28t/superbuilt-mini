package com.superbuilt.mini.issue;

import jakarta.persistence.*;

@Entity
@Table(name = "issue_embeddings")
public class IssueEmbedding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "issue_id",
            nullable = false,
            unique = true
    )
    private Issue issue;

    /*
     * We will add the actual PostgreSQL vector column
     * in the database manually.
     *
     * For now, Hibernate should not try to manage this
     * field directly.
     */
    @Transient
    private float[] embedding;

    public Long getId() {
        return id;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public float[] getEmbedding() {
        return embedding;
    }

    public void setEmbedding(float[] embedding) {
        this.embedding = embedding;
    }
}