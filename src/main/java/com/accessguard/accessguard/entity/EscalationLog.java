package com.accessguard.accessguard.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "escalation_log")
public class EscalationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(name = "revocation_task_id", nullable = false)
    private UUID revocationTaskId;

    @NotNull
    @Column(name = "escalated_to", nullable = false)
    private UUID escalatedTo;

    @NotNull
    @Column(name = "escalated_at", nullable = false)
    private LocalDateTime escalatedAt;

    @NotNull
    @Column(name = "reason", nullable = false, length = 500)
    private String reason;

    public EscalationLog() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getRevocationTaskId() {
        return revocationTaskId;
    }

    public void setRevocationTaskId(UUID revocationTaskId) {
        this.revocationTaskId = revocationTaskId;
    }

    public UUID getEscalatedTo() {
        return escalatedTo;
    }

    public void setEscalatedTo(UUID escalatedTo) {
        this.escalatedTo = escalatedTo;
    }

    public LocalDateTime getEscalatedAt() {
        return escalatedAt;
    }

    public void setEscalatedAt(LocalDateTime escalatedAt) {
        this.escalatedAt = escalatedAt;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}