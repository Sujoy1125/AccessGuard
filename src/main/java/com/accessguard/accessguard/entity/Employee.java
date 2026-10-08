package com.accessguard.accessguard.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "employee")
public class Employee {
    /** Read-only projection; AccessGrantEntity owns writes and grant metadata. */
    @ManyToMany
    @JoinTable(name = "access_grant", joinColumns = @JoinColumn(name = "employee_id"), inverseJoinColumns = @JoinColumn(name = "system_id"))
    @org.hibernate.annotations.Immutable
    @JsonIgnore
    private Set<SystemEntity> grantedSystems = new HashSet<>();

    @JsonIgnore
    public Set<SystemEntity> getGrantedSystems() {
        return grantedSystems;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    @NotBlank
    @Email
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String value) {
        this.email = value;
    }

    @NotBlank
    @Column(name = "department", nullable = false)
    private String department;

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String value) {
        this.department = value;
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EmployeeStatus status;

    public EmployeeStatus getStatus() {
        return status;
    }

    public void setStatus(EmployeeStatus value) {
        this.status = value;
    }

    @Column(name = "last_working_day", nullable = true)
    private LocalDate lastWorkingDay;

    public LocalDate getLastWorkingDay() {
        return lastWorkingDay;
    }

    public void setLastWorkingDay(LocalDate value) {
        this.lastWorkingDay = value;
    }

    @OneToMany(mappedBy = "employee")
    @JsonIgnore
    private List<DeviceAsset> deviceAssets = new ArrayList<>();

    @JsonIgnore
    public List<DeviceAsset> getDeviceAssets() {
        return deviceAssets;
    }

    @OneToMany(mappedBy = "owner")
    @JsonIgnore
    private List<SystemEntity> ownedSystems = new ArrayList<>();

    @JsonIgnore
    public List<SystemEntity> getOwnedSystems() {
        return ownedSystems;
    }

    @OneToMany(mappedBy = "employee")
    @JsonIgnore
    private List<AccessGrantEntity> accessGrants = new ArrayList<>();

    @JsonIgnore
    public List<AccessGrantEntity> getAccessGrants() {
        return accessGrants;
    }

    @OneToMany(mappedBy = "employee")
    @JsonIgnore
    private List<OffboardingCase> offboardingCases = new ArrayList<>();

    @JsonIgnore
    public List<OffboardingCase> getOffboardingCases() {
        return offboardingCases;
    }

    @OneToMany(mappedBy = "assignedOwner")
    @JsonIgnore
    private List<RevocationTask> assignedTasks = new ArrayList<>();

    @JsonIgnore
    public List<RevocationTask> getAssignedTasks() {
        return assignedTasks;
    }

    @OneToMany(mappedBy = "employee")
    @JsonIgnore
    private List<DataAcknowledgment> dataAcknowledgments = new ArrayList<>();

    @JsonIgnore
    public List<DataAcknowledgment> getDataAcknowledgments() {
        return dataAcknowledgments;
    }

    @OneToMany(mappedBy = "employee")
    @JsonIgnore
    private List<HighRiskActivityFlag> highRiskActivityFlags = new ArrayList<>();

    @JsonIgnore
    public List<HighRiskActivityFlag> getHighRiskActivityFlags() {
        return highRiskActivityFlags;
    }

    @OneToMany(mappedBy = "reviewer")
    @JsonIgnore
    private List<HighRiskActivityFlag> reviewedHighRiskActivityFlags = new ArrayList<>();

    @JsonIgnore
    public List<HighRiskActivityFlag> getReviewedHighRiskActivityFlags() {
        return reviewedHighRiskActivityFlags;
    }

    @OneToMany(mappedBy = "escalatedToEmployee")
    @JsonIgnore
    private List<EscalationLog> escalationLogs = new ArrayList<>();

    @JsonIgnore
    public List<EscalationLog> getEscalationLogs() {
        return escalationLogs;
    }
}
