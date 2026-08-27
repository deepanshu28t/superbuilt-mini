package com.superbuilt.mini.issue;

import com.superbuilt.mini.project.Project;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "issues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 5000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueSource source;

    /**
     * Indicates whether the issue requires an architect
     * or project manager to make a decision.
     */
    @Column(nullable = false)
    private boolean requiresDecision;

    /**
     * Confidence of the AI detection/analysis.
     *
     * Example:
     * 0.94 = 94% confidence
     */
    private Double confidenceScore;

    /**
     * Risk score calculated by our deterministic
     * risk engine later.
     */
    private Double riskScore;

    private LocalDateTime dueDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = IssueStatus.OPEN;
        }

        if (requiresDecision) {
            // Nothing for now.
            // This field will be used by the
            // Morning Brief later.
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}