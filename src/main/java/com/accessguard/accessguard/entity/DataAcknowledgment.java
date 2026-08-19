package com.accessguard.accessguard.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "data_acknowledgment")
public class DataAcknowledgment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Plain UUID, not @ManyToOne: OffboardingCase is owned by Member 2
    // and may not exist in the codebase yet. Swap this for a real
    // relationship once the team merges branches, if you want to.
    @NotNull
    @Column(name = "offboarding_case_id", nullable = false)
    private UUID offboardingCaseId;

    @NotNull
    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @NotNull
    @Column(name = "acknowledged_at", nullable = false)
    private LocalDateTime acknowledgedAt;

    @NotNull
    @Column(name = "statement_version", nullable = false, length = 50)
    private String statementVersion;

    public DataAcknowledgment() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOffboardingCaseId() {
        return offboardingCaseId;
    }

    public void setOffboardingCaseId(UUID offboardingCaseId) {
        this.offboardingCaseId = offboardingCaseId;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(UUID employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDateTime getAcknowledgedAt() {
        return acknowledgedAt;
    }

    public void setAcknowledgedAt(LocalDateTime acknowledgedAt) {
        this.acknowledgedAt = acknowledgedAt;
    }

    public String getStatementVersion() {
        return statementVersion;
    }

    public void setStatementVersion(String statementVersion) {
        this.statementVersion = statementVersion;
    }
}