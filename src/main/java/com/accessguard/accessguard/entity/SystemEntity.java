package com.accessguard.accessguard.entity;

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
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "systems")
public class SystemEntity {
    @ManyToMany(mappedBy = "grantedSystems")
    @org.hibernate.annotations.Immutable
    @JsonIgnore
    private Set<Employee> grantedEmployees = new HashSet<>();

    @JsonIgnore
    public Set<Employee> getGrantedEmployees() {
        return grantedEmployees;
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

    @NotNull
    @Column(name = "owner_user_id", nullable = false)
    private UUID ownerUserId;

    public UUID getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(UUID value) {
        this.ownerUserId = value;
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private SystemCategory category;

    public SystemCategory getCategory() {
        return category;
    }

    public void setCategory(SystemCategory value) {
        this.category = value;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id", insertable = false, updatable = false)
    @JsonIgnore
    private Employee owner;

    @JsonIgnore
    public Employee getOwner() {
        return owner;
    }

    @OneToMany(mappedBy = "system")
    @JsonIgnore
    private List<AccessGrantEntity> accessGrants = new ArrayList<>();

    @JsonIgnore
    public List<AccessGrantEntity> getAccessGrants() {
        return accessGrants;
    }

    @OneToMany(mappedBy = "system")
    @JsonIgnore
    private List<RevocationTask> revocationTasks = new ArrayList<>();

    @JsonIgnore
    public List<RevocationTask> getRevocationTasks() {
        return revocationTasks;
    }
}
