package com.accessguard.accessguard.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "device_asset")
public class DeviceAsset {
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
    @Enumerated(EnumType.STRING)
    @Column(name = "asset_type", nullable = false)
    private AssetType assetType;

    public AssetType getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetType value) {
        this.assetType = value;
    }

    @NotBlank
    @Column(name = "return_status", nullable = false)
    private String returnStatus;

    public String getReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(String value) {
        this.returnStatus = value;
    }

    @Column(name = "return_confirmed_at", nullable = true)
    private LocalDateTime returnConfirmedAt;

    public LocalDateTime getReturnConfirmedAt() {
        return returnConfirmedAt;
    }

    public void setReturnConfirmedAt(LocalDateTime value) {
        this.returnConfirmedAt = value;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    @JsonIgnore
    private Employee employee;

    @JsonIgnore
    public Employee getEmployee() {
        return employee;
    }

    @OneToMany(mappedBy = "deviceAsset")
    @JsonIgnore
    private List<AccessGrantEntity> accessGrants = new ArrayList<>();

    @JsonIgnore
    public List<AccessGrantEntity> getAccessGrants() {
        return accessGrants;
    }
}
