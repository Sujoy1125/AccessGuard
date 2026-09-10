package com.accessguard.accessguard.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.UUID;

/** Minimal relationship anchor for the offboarding_case table owned by the offboarding module. */
@Entity
@Table(name = "offboarding_case")
public class OffboardingCase {
    @Id
    private UUID id;
    @OneToOne(mappedBy = "offboardingCase") @JsonIgnore
    private DataAcknowledgment dataAcknowledgment;

    public OffboardingCase() { }
    private OffboardingCase(UUID id) { this.id = id; }
    public static OffboardingCase reference(UUID id) { return new OffboardingCase(id); }
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
}
