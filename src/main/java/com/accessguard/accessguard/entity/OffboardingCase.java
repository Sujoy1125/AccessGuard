package com.accessguard.accessguard.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "offboarding_case")
public class OffboardingCase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @NotNull
    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    public UUID getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(UUID value) {
        this.employeeId = value;
    }

    @NotNull
    @Column(name = "initiated_by", nullable = false)
    private UUID initiatedBy;

    public UUID getInitiatedBy() {
        return initiatedBy;
    }

    public void setInitiatedBy(UUID value) {
        this.initiatedBy = value;
    }

    @NotNull
    @Column(name = "initiated_at", nullable = false)
    private LocalDateTime initiatedAt;

    public LocalDateTime getInitiatedAt() {
        return initiatedAt;
    }

    public void setInitiatedAt(LocalDateTime value) {
        this.initiatedAt = value;
    }

    @NotNull
    @Column(name = "target_completion_date", nullable = false)
    private LocalDate targetCompletionDate;

    public LocalDate getTargetCompletionDate() {
        return targetCompletionDate;
    }

    public void setTargetCompletionDate(LocalDate value) {
        this.targetCompletionDate = value;
    }

    @NotBlank
    @Column(name = "status", nullable = false)
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String value) {
        this.status = value;
    }

    @Column(name = "trigger_flag_id", nullable = true)
    private UUID triggerFlagId;

    public UUID getTriggerFlagId() {
        return triggerFlagId;
    }

    public void setTriggerFlagId(UUID value) {
        this.triggerFlagId = value;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    @JsonIgnore
    private Employee employee;

    @JsonIgnore
    public Employee getEmployee() {
        return employee;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiated_by", insertable = false, updatable = false)
    @JsonIgnore
    private Employee initiator;

    @JsonIgnore
    public Employee getInitiator() {
        return initiator;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trigger_flag_id", insertable = false, updatable = false)
    @JsonIgnore
    private HighRiskActivityFlag triggerFlag;

    @JsonIgnore
    public HighRiskActivityFlag getTriggerFlag() {
        return triggerFlag;
    }

    @OneToMany(mappedBy = "offboardingCase")
    @JsonIgnore
    private List<RevocationTask> revocationTasks = new ArrayList<>();

    @JsonIgnore
    public List<RevocationTask> getRevocationTasks() {
        return revocationTasks;
    }

    @OneToOne(mappedBy = "offboardingCase")
    @JsonIgnore
    private DataAcknowledgment dataAcknowledgment;

    @JsonIgnore
    public DataAcknowledgment getDataAcknowledgment() {
        return dataAcknowledgment;
    }
}
