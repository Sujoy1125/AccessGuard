-- Complete schema for a NEW empty accessguard database.

BEGIN;

CREATE TABLE IF NOT EXISTS access_grant (
    id uuid PRIMARY KEY,
    employee_id uuid NOT NULL,
    system_id uuid NOT NULL,
    granted_at timestamp NOT NULL,
    granted_by uuid NOT NULL,
    access_level varchar(255) NOT NULL,
    device_asset_id uuid
);

CREATE TABLE IF NOT EXISTS audit_snapshot (
    id uuid PRIMARY KEY,
    period_start date NOT NULL,
    period_end date NOT NULL,
    total_offboardings integer DEFAULT 0 NOT NULL,
    pct_fully_revoked_on_time real,
    avg_revocation_time_hrs real,
    overdue_cases_count integer DEFAULT 0 NOT NULL,
    retained_asset_count integer DEFAULT 0 NOT NULL
);

CREATE TABLE IF NOT EXISTS data_acknowledgment (
    id uuid PRIMARY KEY,
    offboarding_case_id uuid NOT NULL UNIQUE,
    employee_id uuid NOT NULL,
    acknowledged_at timestamp NOT NULL,
    statement_version varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS device_asset (
    id uuid PRIMARY KEY,
    employee_id uuid NOT NULL,
    asset_type varchar(255) NOT NULL,
    return_status varchar(255) NOT NULL,
    return_confirmed_at timestamp
);

CREATE TABLE IF NOT EXISTS employee (
    id uuid PRIMARY KEY,
    name varchar(255) NOT NULL,
    email varchar(255) NOT NULL UNIQUE,
    department varchar(255) NOT NULL,
    status varchar(255) NOT NULL,
    last_working_day date
);

CREATE TABLE IF NOT EXISTS escalation_log (
    id uuid PRIMARY KEY,
    revocation_task_id uuid NOT NULL,
    escalated_to uuid NOT NULL,
    escalated_at timestamp NOT NULL,
    reason varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS high_risk_activity_flag (
    id uuid PRIMARY KEY,
    employee_id uuid NOT NULL,
    source_system varchar(255) NOT NULL,
    activity_type varchar(20) NOT NULL,
    detected_at timestamp NOT NULL,
    reviewed_by uuid,
    review_status varchar(255) NOT NULL,
    detected_by uuid
);

CREATE TABLE IF NOT EXISTS offboarding_case (
    id uuid PRIMARY KEY,
    employee_id uuid NOT NULL,
    initiated_by uuid NOT NULL,
    initiated_at timestamp NOT NULL,
    target_completion_date date NOT NULL,
    status varchar(255) NOT NULL,
    trigger_flag_id uuid
);

CREATE TABLE IF NOT EXISTS revocation_task (
    id uuid PRIMARY KEY,
    offboarding_case_id uuid NOT NULL,
    system_id uuid NOT NULL,
    assigned_owner_id uuid NOT NULL,
    status varchar(255) NOT NULL,
    confirmed_at timestamp,
    confirmed_by uuid
);

CREATE TABLE IF NOT EXISTS systems (
    id uuid PRIMARY KEY,
    name varchar(255) NOT NULL,
    owner_user_id uuid NOT NULL,
    category varchar(255) NOT NULL
);

DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_device_asset_employee_id' AND conrelid='device_asset'::regclass) THEN ALTER TABLE device_asset ADD CONSTRAINT fk_device_asset_employee_id FOREIGN KEY (employee_id) REFERENCES employee(id); END IF; END $$;

DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_systems_owner_user_id' AND conrelid='systems'::regclass) THEN ALTER TABLE systems ADD CONSTRAINT fk_systems_owner_user_id FOREIGN KEY (owner_user_id) REFERENCES employee(id); END IF; END $$;

DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_access_grant_employee_id' AND conrelid='access_grant'::regclass) THEN ALTER TABLE access_grant ADD CONSTRAINT fk_access_grant_employee_id FOREIGN KEY (employee_id) REFERENCES employee(id); END IF; END $$;

DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_access_grant_system_id' AND conrelid='access_grant'::regclass) THEN ALTER TABLE access_grant ADD CONSTRAINT fk_access_grant_system_id FOREIGN KEY (system_id) REFERENCES systems(id); END IF; END $$;

DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_access_grant_granted_by' AND conrelid='access_grant'::regclass) THEN ALTER TABLE access_grant ADD CONSTRAINT fk_access_grant_granted_by FOREIGN KEY (granted_by) REFERENCES employee(id); END IF; END $$;

DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_access_grant_device_asset_id' AND conrelid='access_grant'::regclass) THEN ALTER TABLE access_grant ADD CONSTRAINT fk_access_grant_device_asset_id FOREIGN KEY (device_asset_id) REFERENCES device_asset(id); END IF; END $$;

DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_offboarding_case_employee_id' AND conrelid='offboarding_case'::regclass) THEN ALTER TABLE offboarding_case ADD CONSTRAINT fk_offboarding_case_employee_id FOREIGN KEY (employee_id) REFERENCES employee(id); END IF; END $$;

DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_offboarding_case_initiated_by' AND conrelid='offboarding_case'::regclass) THEN ALTER TABLE offboarding_case ADD CONSTRAINT fk_offboarding_case_initiated_by FOREIGN KEY (initiated_by) REFERENCES employee(id); END IF; END $$;

DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_offboarding_case_trigger_flag_id' AND conrelid='offboarding_case'::regclass) THEN ALTER TABLE offboarding_case ADD CONSTRAINT fk_offboarding_case_trigger_flag_id FOREIGN KEY (trigger_flag_id) REFERENCES high_risk_activity_flag(id); END IF; END $$;

DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_revocation_task_offboarding_case_id' AND conrelid='revocation_task'::regclass) THEN ALTER TABLE revocation_task ADD CONSTRAINT fk_revocation_task_offboarding_case_id FOREIGN KEY (offboarding_case_id) REFERENCES offboarding_case(id); END IF; END $$;

DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_revocation_task_system_id' AND conrelid='revocation_task'::regclass) THEN ALTER TABLE revocation_task ADD CONSTRAINT fk_revocation_task_system_id FOREIGN KEY (system_id) REFERENCES systems(id); END IF; END $$;

DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_revocation_task_assigned_owner_id' AND conrelid='revocation_task'::regclass) THEN ALTER TABLE revocation_task ADD CONSTRAINT fk_revocation_task_assigned_owner_id FOREIGN KEY (assigned_owner_id) REFERENCES employee(id); END IF; END $$;

DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_revocation_task_confirmed_by' AND conrelid='revocation_task'::regclass) THEN ALTER TABLE revocation_task ADD CONSTRAINT fk_revocation_task_confirmed_by FOREIGN KEY (confirmed_by) REFERENCES employee(id); END IF; END $$;

DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_data_acknowledgment_employee_id' AND conrelid='data_acknowledgment'::regclass) THEN ALTER TABLE data_acknowledgment ADD CONSTRAINT fk_data_acknowledgment_employee_id FOREIGN KEY (employee_id) REFERENCES employee(id); END IF; END $$;

DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_data_acknowledgment_offboarding_case_id' AND conrelid='data_acknowledgment'::regclass) THEN ALTER TABLE data_acknowledgment ADD CONSTRAINT fk_data_acknowledgment_offboarding_case_id FOREIGN KEY (offboarding_case_id) REFERENCES offboarding_case(id); END IF; END $$;

DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_high_risk_activity_flag_employee_id' AND conrelid='high_risk_activity_flag'::regclass) THEN ALTER TABLE high_risk_activity_flag ADD CONSTRAINT fk_high_risk_activity_flag_employee_id FOREIGN KEY (employee_id) REFERENCES employee(id); END IF; END $$;

DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_high_risk_activity_flag_reviewed_by' AND conrelid='high_risk_activity_flag'::regclass) THEN ALTER TABLE high_risk_activity_flag ADD CONSTRAINT fk_high_risk_activity_flag_reviewed_by FOREIGN KEY (reviewed_by) REFERENCES employee(id); END IF; END $$;

DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_high_risk_activity_flag_detected_by' AND conrelid='high_risk_activity_flag'::regclass) THEN ALTER TABLE high_risk_activity_flag ADD CONSTRAINT fk_high_risk_activity_flag_detected_by FOREIGN KEY (detected_by) REFERENCES employee(id); END IF; END $$;

DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_escalation_log_revocation_task_id' AND conrelid='escalation_log'::regclass) THEN ALTER TABLE escalation_log ADD CONSTRAINT fk_escalation_log_revocation_task_id FOREIGN KEY (revocation_task_id) REFERENCES revocation_task(id); END IF; END $$;

DO $$ BEGIN IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_escalation_log_escalated_to' AND conrelid='escalation_log'::regclass) THEN ALTER TABLE escalation_log ADD CONSTRAINT fk_escalation_log_escalated_to FOREIGN KEY (escalated_to) REFERENCES employee(id); END IF; END $$;

CREATE INDEX IF NOT EXISTS ix_device_asset_employee_id ON device_asset (employee_id);
CREATE INDEX IF NOT EXISTS ix_systems_owner_user_id ON systems (owner_user_id);
CREATE INDEX IF NOT EXISTS ix_access_grant_employee_id ON access_grant (employee_id);
CREATE INDEX IF NOT EXISTS ix_access_grant_system_id ON access_grant (system_id);
CREATE INDEX IF NOT EXISTS ix_access_grant_granted_by ON access_grant (granted_by);
CREATE INDEX IF NOT EXISTS ix_access_grant_device_asset_id ON access_grant (device_asset_id);
CREATE INDEX IF NOT EXISTS ix_offboarding_case_employee_id ON offboarding_case (employee_id);
CREATE INDEX IF NOT EXISTS ix_offboarding_case_initiated_by ON offboarding_case (initiated_by);
CREATE INDEX IF NOT EXISTS ix_offboarding_case_trigger_flag_id ON offboarding_case (trigger_flag_id);
CREATE INDEX IF NOT EXISTS ix_revocation_task_offboarding_case_id ON revocation_task (offboarding_case_id);
CREATE INDEX IF NOT EXISTS ix_revocation_task_system_id ON revocation_task (system_id);
CREATE INDEX IF NOT EXISTS ix_revocation_task_assigned_owner_id ON revocation_task (assigned_owner_id);
CREATE INDEX IF NOT EXISTS ix_revocation_task_confirmed_by ON revocation_task (confirmed_by);
CREATE INDEX IF NOT EXISTS ix_data_acknowledgment_employee_id ON data_acknowledgment (employee_id);
CREATE INDEX IF NOT EXISTS ix_data_acknowledgment_offboarding_case_id ON data_acknowledgment (offboarding_case_id);
CREATE INDEX IF NOT EXISTS ix_high_risk_activity_flag_employee_id ON high_risk_activity_flag (employee_id);
CREATE INDEX IF NOT EXISTS ix_high_risk_activity_flag_reviewed_by ON high_risk_activity_flag (reviewed_by);
CREATE INDEX IF NOT EXISTS ix_high_risk_activity_flag_detected_by ON high_risk_activity_flag (detected_by);
CREATE INDEX IF NOT EXISTS ix_escalation_log_revocation_task_id ON escalation_log (revocation_task_id);
CREATE INDEX IF NOT EXISTS ix_escalation_log_escalated_to ON escalation_log (escalated_to);
COMMIT;
