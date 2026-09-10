package com.accessguard.accessguard.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

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
    private Integer totalOffboardings = 0;

    @Column(name = "pct_fully_revoked_on_time")
    private Float pctFullyRevokedOnTime;

    @Column(name = "avg_revocation_time_hrs")
    private Float avgRevocationTimeHrs;

    @Column(name = "overdue_cases_count", nullable = false)
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
}
