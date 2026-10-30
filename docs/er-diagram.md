# AccessGuard ER diagram

```mermaid
erDiagram
    EMPLOYEE ||--o{ DEVICE_ASSET : assigned
    EMPLOYEE ||--o{ SYSTEM : owns
    EMPLOYEE ||--o{ ACCESS_GRANT : receives
    EMPLOYEE ||--o{ HIGH_RISK_ACTIVITY_FLAG : flagged
    EMPLOYEE ||--o{ OFFBOARDING_CASE : involves
    EMPLOYEE ||--o{ REVOCATION_TASK : assigned_owner
    EMPLOYEE ||--o{ DATA_ACKNOWLEDGMENT : signs
    DEVICE_ASSET o|--o{ ACCESS_GRANT : associated
    SYSTEM ||--o{ ACCESS_GRANT : grants
    SYSTEM ||--o{ REVOCATION_TASK : revoked
    HIGH_RISK_ACTIVITY_FLAG o|--o{ OFFBOARDING_CASE : triggers
    OFFBOARDING_CASE ||--o{ REVOCATION_TASK : contains
    OFFBOARDING_CASE ||--o| DATA_ACKNOWLEDGMENT : requires
    REVOCATION_TASK ||--o{ ESCALATION_LOG : escalates
    ACCESS_GRANT {
        UUID id PK
        UUID employeeId FK
        UUID systemId FK
        LocalDateTime grantedAt
        UUID grantedBy FK
        String accessLevel
        UUID deviceAssetId FK
    }
    AUDIT_SNAPSHOT {
        UUID id PK
        LocalDate periodStart
        LocalDate periodEnd
        Integer totalOffboardings
        Float pctFullyRevokedOnTime
        Float avgRevocationTimeHrs
        Integer overdueCasesCount
        Integer retainedAssetCount
    }
    DATA_ACKNOWLEDGMENT {
        UUID id PK
        UUID offboardingCaseId FK
        UUID employeeId FK
        LocalDateTime acknowledgedAt
        String statementVersion
    }
    DEVICE_ASSET {
        UUID id PK
        UUID employeeId FK
        AssetType assetType
        String returnStatus
        LocalDateTime returnConfirmedAt
    }
    EMPLOYEE {
        UUID id PK
        String name
        String email
        String department
        EmployeeStatus status
        LocalDate lastWorkingDay
    }
    ESCALATION_LOG {
        UUID id PK
        UUID revocationTaskId FK
        UUID escalatedTo FK
        LocalDateTime escalatedAt
        String reason
    }
    HIGH_RISK_ACTIVITY_FLAG {
        UUID id PK
        UUID employeeId FK
        String sourceSystem
        ActivityType activityType
        LocalDateTime detectedAt
        UUID reviewedBy FK
        String reviewStatus
        UUID detectedBy FK
    }
    OFFBOARDING_CASE {
        UUID id PK
        UUID employeeId FK
        UUID initiatedBy FK
        LocalDateTime initiatedAt
        LocalDate targetCompletionDate
        String status
        UUID triggerFlagId FK
    }
    REVOCATION_TASK {
        UUID id PK
        UUID offboardingCaseId FK
        UUID systemId FK
        UUID assignedOwnerId FK
        String status
        LocalDateTime confirmedAt
        UUID confirmedBy FK
    }
    SYSTEM {
        UUID id PK
        String name
        UUID ownerUserId FK
        SystemCategory category
    }
```

All actor UUIDs reference Employee. The employee/system many-to-many relationship is stored through AccessGrant, which owns grant metadata. The JPA many-to-many collections are immutable read-only projections. Optional deviceAssetId and triggerFlagId implement the two relationships missing foreign keys in the supplied attributes.
