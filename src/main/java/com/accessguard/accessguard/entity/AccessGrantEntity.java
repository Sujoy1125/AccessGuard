package com.accessguard.accessguard.entity;

import java.time.LocalDateTime;
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
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "access_grant")
public class AccessGrantEntity {
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
    @Column(name = "system_id", nullable = false)
    private UUID systemId;

    public UUID getSystemId() {
        return systemId;
    }

    public void setSystemId(UUID value) {
        this.systemId = value;
    }

    @NotNull
    @Column(name = "granted_at", nullable = false)
    private LocalDateTime grantedAt;

    public LocalDateTime getGrantedAt() {
        return grantedAt;
    }

    public void setGrantedAt(LocalDateTime value) {
        this.grantedAt = value;
    }

    @NotNull
    @Column(name = "granted_by", nullable = false)
    private UUID grantedBy;

    public UUID getGrantedBy() {
        return grantedBy;
    }

    public void setGrantedBy(UUID value) {
        this.grantedBy = value;
    }

    @NotBlank
    @Column(name = "access_level", nullable = false)
    private String accessLevel;

    public String getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(String value) {
        this.accessLevel = value;
    }

    @Column(name = "device_asset_id", nullable = true)
    private UUID deviceAssetId;

    public UUID getDeviceAssetId() {
        return deviceAssetId;
    }

    public void setDeviceAssetId(UUID value) {
        this.deviceAssetId = value;
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
    @JoinColumn(name = "system_id", insertable = false, updatable = false)
    @JsonIgnore
    private SystemEntity system;

    @JsonIgnore
    public SystemEntity getSystem() {
        return system;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "granted_by", insertable = false, updatable = false)
    @JsonIgnore
    private Employee grantor;

    @JsonIgnore
    public Employee getGrantor() {
        return grantor;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_asset_id", insertable = false, updatable = false)
    @JsonIgnore
    private DeviceAsset deviceAsset;

    @JsonIgnore
    public DeviceAsset getDeviceAsset() {
        return deviceAsset;
    }
}
