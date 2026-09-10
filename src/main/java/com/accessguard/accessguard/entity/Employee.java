package com.accessguard.accessguard.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Minimal relationship anchor for the employee table owned by the employee module. */
@Entity
@Table(name = "employee")
public class Employee {
    @Id
    private UUID id;

    @OneToMany(mappedBy = "employee") @JsonIgnore
    private List<DataAcknowledgment> dataAcknowledgments = new ArrayList<>();
    @OneToMany(mappedBy = "employee") @JsonIgnore
    private List<HighRiskActivityFlag> highRiskActivityFlags = new ArrayList<>();
    @OneToMany(mappedBy = "reviewer") @JsonIgnore
    private List<HighRiskActivityFlag> reviewedHighRiskActivityFlags = new ArrayList<>();
    @OneToMany(mappedBy = "escalatedToEmployee") @JsonIgnore
    private List<EscalationLog> escalationLogs = new ArrayList<>();

    public Employee() { }
    private Employee(UUID id) { this.id = id; }
    public static Employee reference(UUID id) { return new Employee(id); }
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
}
