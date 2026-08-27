package com.superbuilt.mini.rfi;

import com.superbuilt.mini.document.Discipline;
import com.superbuilt.mini.project.Project;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "rfis",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_project_rfi_number",
                        columnNames = {"project_id", "rfi_number"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RFI {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rfi_number", nullable = false)
    private String rfiNumber;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 5000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Discipline discipline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RFIStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RFIPriority priority;

    private LocalDate dueDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {

        createdAt = LocalDateTime.now();

        if (status == null) {
            status = RFIStatus.OPEN;
        }

        if (priority == null) {
            priority = RFIPriority.MEDIUM;
        }
    }
}