# Verification results

Verified on 8 October 2026.

- `mvnw.cmd -o clean package`: BUILD SUCCESS, 5 tests, 0 failures, 0 errors. Produced the executable Spring Boot JAR at `target/accessguard-0.0.1-SNAPSHOT.jar`.
- HTTP integration test exercises collection GET, item GET, POST, PUT, PATCH and DELETE for all ten entities, Swagger generation, duplicate acknowledgments, invalid foreign keys, invalid email, immutable id rejection, deletion conflicts, and preservation of omitted PATCH counters.
- Persistence tests verify one-to-one acknowledgments, employee/device/grant/system mappings, many-to-many projections, flag/case mappings, case/task mappings and owner references.
- PostgreSQL 18.3 single-user verification passed for the full schema, migration from the original Member 3 schema with three placeholder tables, and repeat execution of both scripts. Logs: `work/postgres-single-*.log`.
- Application API and mapping tests used H2. A live PostgreSQL application test was not completed because temporary TCP server startup was unreliable in this execution environment. SQL verification used the actual PostgreSQL engine in single-user mode.
- The user's installed PostgreSQL database and data were not modified. Temporary database files are stored under `work/` within this project.

Build log: `work/build-final.log`. Original source backup: `work/original-src`. Prior uncommitted edits: `work/pre-integration.patch`.
