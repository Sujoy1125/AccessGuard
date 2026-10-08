# AccessGuard

One Spring Boot application containing all ten entities. Business logic lives directly in controllers; controllers call JPA repositories. No service layer remains. The original sources and uncommitted changes are backed up under `work/original-src` and `work/pre-integration.patch`.

## Run

Requires Java 25 or newer, PostgreSQL, and the Maven wrapper included here.

1. Create the database with `psql -U postgres -c "CREATE DATABASE accessguard;"` if it does not exist.
2. For a NEW database, execute `psql -U postgres -d accessguard -v ON_ERROR_STOP=1 -f database/full-schema.sql`.
3. For the existing Member 3 database, execute `psql -U postgres -d accessguard -v ON_ERROR_STOP=1 -f database/missing-entities.sql`. This adds missing tables and expands the three placeholder tables. Existing placeholder records need real field values before their new columns can be made NOT NULL. Constraint creation rejects orphan foreign keys instead of deleting data. This script assumes all four Member 3 tables already exist.
4. Set `$env:DB_PASSWORD='your PostgreSQL password'` in PowerShell. The datasource remains `jdbc:postgresql://localhost:5432/accessguard`, user `postgres`; edit application.properties if your connection differs.
5. Run `.\mvnw.cmd spring-boot:run`, or `java -jar target/accessguard-0.0.1-SNAPSHOT.jar` after building.

Swagger: http://localhost:8080/swagger-ui/index.html. OpenAPI JSON: http://localhost:8080/v3/api-docs.

## API contract

Every collection supports GET and POST; every `/{id}` supports GET, PUT, PATCH, DELETE. POST returns 201, DELETE 204, missing records 404, validation errors 400, and database conflicts 409. IDs are generated UUIDs; foreign keys are flat UUID fields in JSON. POST ignores a supplied id. PUT replaces scalar fields and requires mandatory values. PATCH accepts an object, preserves omitted fields, allows explicit null for optional fields, validates the merged record, and rejects unknown fields or id changes. Relationships are excluded from JSON to prevent recursion.

| Entity | Collection |
| --- | --- |
| Employee | /api/employees |
| DeviceAsset | /api/device-assets |
| System | /api/systems |
| AccessGrant | /api/access-grants |
| OffboardingCase | /api/offboarding-cases |
| RevocationTask | /api/revocation-tasks |
| DataAcknowledgment | /api/data-acknowledgments |
| HighRiskActivityFlag | /api/high-risk-activity-flags |
| EscalationLog | /api/escalation-logs |
| AuditSnapshot | /api/audit-snapshots |

Create parent records before child records; delete children before parents. Referenced parents are protected by database foreign keys. An acknowledgment's employee must match its case; trigger flags and device grants must also match the associated employee. confirmedAt and confirmedBy must be supplied or cleared together.

Member 1 enums: employee status ACTIVE/DEPARTING/OFFBOARDED; asset type LAPTOP/EXTERNAL_DRIVE/PHONE; system category CORE_IT/SAAS/PHYSICAL/CLOUD. Member 3 activity types BULK_DOWNLOAD/EXPORT and review statuses PENDING/REVIEWED/DISMISSED are retained. Other workflow status fields are nonblank strings because no vocabulary was supplied.

`reviewedBy` and `overdueCasesCount` are retained; `detectedBy` and `retainedAssetCount` are added. detectedBy is nullable for backward compatibility with older flags. Optional deviceAssetId and triggerFlagId implement the requested asset/grant and flag/case relationships. Tables use the existing singular Member 3 names, plus `systems`.

## Weekly completion and verification

| Week | Implementation |
| --- | --- |
| 1 | Requirements in this README, docs/er-diagram.md, PostgreSQL scripts, unified Spring Boot setup, all ten entities |
| 2 | Six REST operations per entity; real HTTP integration test exercises all ten |
| 3 | Request logging and controller logging, global exceptions, validation on create/replace/patch, Swagger/OpenAPI |
| 4 | One-to-one, one-to-many, many-to-one, immutable many-to-many projections through AccessGrant; persistence and HTTP tests; executable JAR |

Build and test: `.\mvnw.cmd clean package`. Tests use an isolated H2 database by default. Tests explicitly select dependency injection, transaction and context cleanup listeners, avoiding unnecessary Mockito agent attachment on Java 26. API test source is `src/test/java/com/accessguard/accessguard/ApiIntegrationTests.java`. Full build output is under work/build-final.log and Maven reports under target/surefire-reports.

The project models and stores offboarding cases, revocation confirmations, flags, escalations, and snapshots. It does not automatically revoke access in external systems or schedule escalations; those integrations were not supplied.

Detailed verification results and the PostgreSQL testing limitation are recorded in `docs/verification.md`.
