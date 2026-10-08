package com.accessguard.accessguard.entity;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "audit_snapshot")
public class AuditSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @NotNull
    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    @Column(name = "total_offboardings", nullable = false)
    @jakarta.validation.constraints.NotNull
    @jakarta.validation.constraints.Min(0)
    private Integer totalOffboardings = 0;

    @Column(name = "pct_fully_revoked_on_time")
    @jakarta.validation.constraints.DecimalMin("0")
    @jakarta.validation.constraints.DecimalMax("100")
    private Float pctFullyRevokedOnTime;

    @Column(name = "avg_revocation_time_hrs")
    @jakarta.validation.constraints.DecimalMin("0")
    private Float avgRevocationTimeHrs;

    @Column(name = "overdue_cases_count", nullable = false)
    @jakarta.validation.constraints.NotNull
    @jakarta.validation.constraints.Min(0)
    private Integer overdueCasesCount = 0;

    public AuditSnapshot() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(LocalDate periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }

    public Integer getTotalOffboardings() {
        return totalOffboardings;
    }

    public void setTotalOffboardings(Integer totalOffboardings) {
        this.totalOffboardings = totalOffboardings;
    }

    public Float getPctFullyRevokedOnTime() {
        return pctFullyRevokedOnTime;
    }

    public void setPctFullyRevokedOnTime(Float pctFullyRevokedOnTime) {
        this.pctFullyRevokedOnTime = pctFullyRevokedOnTime;
    }

    public Float getAvgRevocationTimeHrs() {
        return avgRevocationTimeHrs;
    }

    public void setAvgRevocationTimeHrs(Float avgRevocationTimeHrs) {
        this.avgRevocationTimeHrs = avgRevocationTimeHrs;
    }

    public Integer getOverdueCasesCount() {
        return overdueCasesCount;
    }

    public void setOverdueCasesCount(Integer overdueCasesCount) {
        this.overdueCasesCount = overdueCasesCount;
    }

    @jakarta.validation.constraints.NotNull
    @jakarta.validation.constraints.Min(0)
    @Column(name = "retained_asset_count", nullable = false)
    private Integer retainedAssetCount = 0;

    public Integer getRetainedAssetCount() {
        return retainedAssetCount;
    }

    public void setRetainedAssetCount(Integer value) {
        retainedAssetCount = value;
    }
}
