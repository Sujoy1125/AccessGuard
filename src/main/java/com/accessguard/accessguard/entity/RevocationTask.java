package com.accessguard.accessguard.entity;

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
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "revocation_task")
public class RevocationTask {
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
    @Column(name = "offboarding_case_id", nullable = false)
    private UUID offboardingCaseId;

    public UUID getOffboardingCaseId() {
        return offboardingCaseId;
    }

    public void setOffboardingCaseId(UUID value) {
        this.offboardingCaseId = value;
    }

    @NotNull
    @Column(name = "system_id", nullable = false)
    private UUID systemId;

    public UUID getSystemId() {
        return systemId;
    }

    public void setSystemId(UUID value) {
        this.systemId = value;
    }

    @NotNull
    @Column(name = "assigned_owner_id", nullable = false)
    private UUID assignedOwnerId;

    public UUID getAssignedOwnerId() {
        return assignedOwnerId;
    }

    public void setAssignedOwnerId(UUID value) {
        this.assignedOwnerId = value;
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

    @Column(name = "confirmed_at", nullable = true)
    private LocalDateTime confirmedAt;

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(LocalDateTime value) {
        this.confirmedAt = value;
    }

    @Column(name = "confirmed_by", nullable = true)
    private UUID confirmedBy;

    public UUID getConfirmedBy() {
        return confirmedBy;
    }

    public void setConfirmedBy(UUID value) {
        this.confirmedBy = value;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offboarding_case_id", insertable = false, updatable = false)
    @JsonIgnore
    private OffboardingCase offboardingCase;

    @JsonIgnore
    public OffboardingCase getOffboardingCase() {
        return offboardingCase;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "system_id", insertable = false, updatable = false)
    @JsonIgnore
    private SystemEntity system;

    @JsonIgnore
    public SystemEntity getSystem() {
        return system;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_owner_id", insertable = false, updatable = false)
    @JsonIgnore
    private Employee assignedOwner;

    @JsonIgnore
    public Employee getAssignedOwner() {
        return assignedOwner;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confirmed_by", insertable = false, updatable = false)
    @JsonIgnore
    private Employee confirmer;

    @JsonIgnore
    public Employee getConfirmer() {
        return confirmer;
    }

    @OneToMany(mappedBy = "revocationTask")
    @JsonIgnore
    private List<EscalationLog> escalationLogs = new ArrayList<>();

    @JsonIgnore
    public List<EscalationLog> getEscalationLogs() {
        return escalationLogs;
    }
}
