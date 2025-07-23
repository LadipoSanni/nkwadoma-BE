-- Enable uuid-ossp extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Creating program_loan_detail_entity table
CREATE TABLE IF NOT EXISTS program_loan_detail_entity (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4(),
    program_id VARCHAR(36) UNIQUE NOT NULL,
    total_amount_requested DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    total_outstanding_amount DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    total_amount_received DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    total_amount_repaid DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_program FOREIGN KEY (program_id) REFERENCES program_entity(id)
    );

-- Creating organization_loan_detail_entity table
CREATE TABLE IF NOT EXISTS organization_loan_detail_entity (
     id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4(),
    organization_id VARCHAR(36) UNIQUE NOT NULL,
    total_amount_requested DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    total_outstanding_amount DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    total_amount_received DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    total_amount_repaid DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_organization FOREIGN KEY (organization_id) REFERENCES organization(id)
    );

-- Inserting into program_loan_detail_entity with aggregated CohortLoanDetailEntity data
INSERT INTO program_loan_detail_entity (
    id,
    program_id,
    total_amount_requested,
    total_outstanding_amount,
    total_amount_received,
    total_amount_repaid
)
SELECT
    uuid_generate_v4(),
    p.id,
    COALESCE((
                 SELECT SUM(cld.total_amount_requested)
                 FROM cohort_loan_detail_entity cld
                          JOIN cohort_entity c ON cld.cohort_id = c.id
                 WHERE c.program_id = p.id
             ), 0.00),
    COALESCE((
                 SELECT SUM(cld.total_outstanding_amount)
                 FROM cohort_loan_detail_entity cld
                          JOIN cohort_entity c ON cld.cohort_id = c.id
                 WHERE c.program_id = p.id
             ), 0.00),
    COALESCE((
                 SELECT SUM(cld.total_amount_received)
                 FROM cohort_loan_detail_entity cld
                          JOIN cohort_entity c ON cld.cohort_id = c.id
                 WHERE c.program_id = p.id
             ), 0.00),
    COALESCE((
                 SELECT SUM(cld.total_amount_repaid)
                 FROM cohort_loan_detail_entity cld
                          JOIN cohort_entity c ON cld.cohort_id = c.id
                 WHERE c.program_id = p.id
             ), 0.00)
FROM program_entity p;

-- Inserting into organization_loan_detail_entity with aggregated ProgramLoanDetailEntity data
INSERT INTO organization_loan_detail_entity (
    id,
    organization_id,
    total_amount_requested,
    total_outstanding_amount,
    total_amount_received,
    total_amount_repaid
)
SELECT
    uuid_generate_v4(),
    o.id,
    COALESCE((
                 SELECT SUM(pld.total_amount_requested)
                 FROM program_loan_detail_entity pld
                          JOIN program_entity p ON pld.program_id = p.id
                 WHERE p.organization_identity_id = o.id
             ), 0.00),
    COALESCE((
                 SELECT SUM(pld.total_outstanding_amount)
                 FROM program_loan_detail_entity pld
                          JOIN program_entity p ON pld.program_id = p.id
                 WHERE p.organization_identity_id = o.id
             ), 0.00),
    COALESCE((
                 SELECT SUM(pld.total_amount_received)
                 FROM program_loan_detail_entity pld
                          JOIN program_entity p ON pld.program_id = p.id
                 WHERE p.organization_identity_id = o.id
             ), 0.00),
    COALESCE((
                 SELECT SUM(pld.total_amount_repaid)
                 FROM program_loan_detail_entity pld
                          JOIN program_entity p ON pld.program_id = p.id
                 WHERE p.organization_identity_id = o.id
             ), 0.00)
FROM organization o;

-- Alter cohort_loan_detail_entity to ensure id is VARCHAR(36)
ALTER TABLE cohort_loan_detail_entity
ALTER COLUMN id TYPE VARCHAR(36) USING (id::VARCHAR(36));

-- Optionally set default for cohort_loan_detail_entity.id
ALTER TABLE cohort_loan_detail_entity
    ALTER COLUMN id SET DEFAULT uuid_generate_v4();