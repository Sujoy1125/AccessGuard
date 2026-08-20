package com.accessguard.accessguard.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "high_risk_activity_flag")
public class HighRiskActivityFlag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @NotNull
    @Column(name = "source_system", nullable = false, length = 100)
    private String sourceSystem;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false, length = 20)
    private ActivityType activityType;

    @NotNull
    @Column(name = "detected_at", nullable = false)
    private LocalDateTime detectedAt;

    // Nullable: no reviewer assigned yet until someone looks at the flag
    @Column(name = "reviewed_by")
    private UUID reviewedBy;

    @jakarta.validation.constraints.Pattern(regexp = "PENDING|REVIEWED|DISMISSED", message = "reviewStatus must be PENDING, REVIEWED, or DISMISSED")
    @Column(name = "review_status", nullable = false, length = 30)
    private String reviewStatus = "PENDING";

    public HighRiskActivityFlag() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(UUID employeeId) {
        this.employeeId = employeeId;
    }

    public String getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public LocalDateTime getDetectedAt() {
        return detectedAt;
    }

    public void setDetectedAt(LocalDateTime detectedAt) {
        this.detectedAt = detectedAt;
    }

    public UUID getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(UUID reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public String getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(String reviewStatus) {
        this.reviewStatus = reviewStatus;
    }
}