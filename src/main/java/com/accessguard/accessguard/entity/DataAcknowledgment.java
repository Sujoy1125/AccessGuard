package com.accessguard.accessguard.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "data_acknowledgment")
public class DataAcknowledgment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(name = "offboarding_case_id", nullable = false, unique = true)
    private UUID offboardingCaseId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offboarding_case_id", insertable = false, updatable = false)
    @JsonIgnore
    private OffboardingCase offboardingCase;

    @NotNull
    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    @JsonIgnore
    private Employee employee;

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

    @JsonIgnore
    public OffboardingCase getOffboardingCase() { return offboardingCase; }

    public void setOffboardingCase(OffboardingCase offboardingCase) {
        this.offboardingCase = offboardingCase;
        this.offboardingCaseId = offboardingCase == null ? null : offboardingCase.getId();
    }

    @JsonIgnore
    public Employee getEmployee() { return employee; }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        this.employeeId = employee == null ? null : employee.getId();
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
