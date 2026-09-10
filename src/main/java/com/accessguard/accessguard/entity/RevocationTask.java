package com.accessguard.accessguard.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Minimal relationship anchor for the revocation_task table owned by the task module. */
@Entity
@Table(name = "revocation_task")
public class RevocationTask {
    @Id
    private UUID id;
    @OneToMany(mappedBy = "revocationTask") @JsonIgnore
    private List<EscalationLog> escalationLogs = new ArrayList<>();

    public RevocationTask() { }
    private RevocationTask(UUID id) { this.id = id; }
    public static RevocationTask reference(UUID id) { return new RevocationTask(id); }
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
}
