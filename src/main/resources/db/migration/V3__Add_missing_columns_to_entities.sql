-- Create all tables first to avoid missing relation errors
CREATE TABLE IF NOT EXISTS bank_detail_entity (
    id VARCHAR(255) NOT NULL,
    bank_name VARCHAR(255),
    bank_number VARCHAR(255),
    CONSTRAINT bank_detail_entity_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS cooperation_entity (
    id VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    CONSTRAINT cooperation_entity_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS financier_entity (
    id VARCHAR(255) NOT NULL,
    activation_status VARCHAR(255),
    accreditation_status VARCHAR(255),
    financier_type VARCHAR(255),
    user_identity_id VARCHAR(255),
    total_amount_invested numeric(38,2),
    declaration_and_agreement BOOLEAN,
    politically_exposed BOOLEAN,
    cooperation_id VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT financier_entity_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS financier_entity_source_of_funds (
    financier_entity_id VARCHAR(255) NOT NULL,
    source_of_funds VARCHAR(255),
    CONSTRAINT fk_financier_entity FOREIGN KEY (financier_entity_id) REFERENCES financier_entity(id)
);

CREATE TABLE IF NOT EXISTS beneficial_owner_entity (
    id VARCHAR(255) NOT NULL,
    beneficial_owner_type VARCHAR(255),
    entity_name VARCHAR(255),
    beneficial_rc_number VARCHAR(255),
    country_of_incorporation VARCHAR(255),
    beneficial_owner_first_name VARCHAR(255),
    beneficial_owner_last_name VARCHAR(255),
    beneficial_owner_relationship VARCHAR(255),
    beneficial_owner_date_of_birth TIMESTAMP WITHOUT TIME ZONE,
    percentage_ownership_or_share DOUBLE PRECISION DEFAULT 0.0,
    voters_card VARCHAR(255),
    national_id_card VARCHAR(255),
    driver_license VARCHAR(255),
    CONSTRAINT beneficial_owner_entity_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS financier_beneficial_owner_entity (
    id VARCHAR(255) NOT NULL,
    financier_entity_id VARCHAR(255),
    beneficial_owner_entity_id VARCHAR(255),
    CONSTRAINT financier_beneficial_owner_entity_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS politically_exposed_person_entity (
    id VARCHAR(255) NOT NULL,
    position_held VARCHAR(255),
    country VARCHAR(255),
    relationship VARCHAR(255),
    additional_information VARCHAR(255),
    CONSTRAINT politically_exposed_person_entity_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS financier_politically_exposed_person_entity (
    id VARCHAR(255) NOT NULL,
    financier_id VARCHAR(255),
    politically_exposed_person_id VARCHAR(255),
    CONSTRAINT financier_politically_exposed_person_entity_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS coupon_distribution_entity (
    id VARCHAR(255) NOT NULL,
    due INTEGER,
    paid INTEGER,
    last_date_paid TIMESTAMP WITHOUT TIME ZONE,
    last_date_due TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT coupon_distribution_entity_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS capital_distribution_entity (
    id VARCHAR(255) NOT NULL,
    due INTEGER,
    total_capital_paid_out numeric(38,2),
    CONSTRAINT capital_distribution_entity_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS vehicle_operation_entity (
    id VARCHAR(255) NOT NULL,
    coupon_distribution_status VARCHAR(255),
    coupon_distribution_id VARCHAR(255),
    fund_raising_status VARCHAR(255),
    deploying_status VARCHAR(255),
    operation_status VARCHAR(255),
    CONSTRAINT vehicle_operation_entity_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS vehicle_closure_entity (
    id VARCHAR(255) NOT NULL,
    recollection_status VARCHAR(255),
    capital_distribution_id VARCHAR(255),
    maturity VARCHAR(255),
    CONSTRAINT vehicle_closure_entity_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS meedl_notification_entity (
    id VARCHAR(255) NOT NULL,
    title VARCHAR(255),
    content_id VARCHAR(255),
    meedl_user VARCHAR(255) NOT NULL,
    read BOOLEAN,
    timestamp TIMESTAMP WITHOUT TIME ZONE,
    call_to_action BOOLEAN,
    sender_email VARCHAR(255),
    sender_full_name VARCHAR(255),
    content_detail VARCHAR(255),
    notification_flag VARCHAR(255),
    CONSTRAINT meedl_notification_entity_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS portfolio_entity (
    id VARCHAR(255) NOT NULL,
    portfolio_name VARCHAR(255),
    total_number_of_investment_vehicle INTEGER DEFAULT 0,
    total_number_of_commercial_funds_investment_vehicle INTEGER DEFAULT 0,
    total_number_of_endowment_funds_investment_vehicle INTEGER DEFAULT 0,
    total_number_of_financier INTEGER DEFAULT 0,
    total_number_of_individual_financier INTEGER DEFAULT 0,
    total_number_of_institutional_financier INTEGER DEFAULT 0,
    total_number_of_loans INTEGER DEFAULT 0,
    loan_referral_percentage DOUBLE PRECISION DEFAULT 0.0,
    loan_request_percentage DOUBLE PRECISION DEFAULT 0.0,
    loan_disbursal_percentage DOUBLE PRECISION DEFAULT 0.0,
    CONSTRAINT portfolio_entity_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS repayment_history_entity (
    id VARCHAR(255) NOT NULL,
    loanee_id VARCHAR(255),
    payment_date_time TIMESTAMP WITHOUT TIME ZONE,
    cohort_id VARCHAR(255),
    amount_paid numeric(38,2),
    total_amount_repaid numeric(38,2),
    amount_outstanding numeric(38,2),
    mode_of_payment VARCHAR(255),
    CONSTRAINT repayment_history_entity_pkey PRIMARY KEY (id)
);

-- Add all missing columns to existing tables
ALTER TABLE meedl_user
    ADD COLUMN IF NOT EXISTS tax_id VARCHAR(255),
    ADD COLUMN IF NOT EXISTS bank_detail_entity_id VARCHAR(255),
    ADD COLUMN IF NOT EXISTS next_of_kin_entity_id VARCHAR(255),
    ADD COLUMN IF NOT EXISTS address VARCHAR(255);

ALTER TABLE loanee_entity
    ADD COLUMN IF NOT EXISTS deferral_approved BOOLEAN DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS defer_reason VARCHAR(255),
    ADD COLUMN IF NOT EXISTS deferral_requested BOOLEAN DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS deferred_date_and_time TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS dropout_approved BOOLEAN DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS dropout_requested BOOLEAN DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS reason_for_dropout VARCHAR(255),
    ADD COLUMN IF NOT EXISTS onboarding_mode VARCHAR(255),
    ADD COLUMN IF NOT EXISTS uploaded_status VARCHAR(255);

ALTER TABLE investment_vehicle_entity
    ADD COLUMN IF NOT EXISTS created_date TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS investment_vehicle_status VARCHAR(255),
    ADD COLUMN IF NOT EXISTS investment_vehicle_link VARCHAR(255),
    ADD COLUMN IF NOT EXISTS operation_id VARCHAR(255),
    ADD COLUMN IF NOT EXISTS closure_id VARCHAR(255),
    ADD COLUMN IF NOT EXISTS main_account_id VARCHAR(255),
    ADD COLUMN IF NOT EXISTS syncing_account_id VARCHAR(255),
    ADD COLUMN IF NOT EXISTS last_updated_date TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS investment_vehicle_visibility VARCHAR(255),
    ADD COLUMN IF NOT EXISTS talent_funded INTEGER DEFAULT 0;

ALTER TABLE investment_vehicle_financier_entity
    ADD COLUMN IF NOT EXISTS amount_invested numeric(38,2),
    ADD COLUMN IF NOT EXISTS financier_id VARCHAR(255),
    ADD COLUMN IF NOT EXISTS investment_vehicle_id VARCHAR(255),
    ADD COLUMN IF NOT EXISTS date_invested date;

ALTER TABLE loanee_loan_detail_entity
    ADD COLUMN IF NOT EXISTS amount_received numeric(38,2),
    ADD COLUMN IF NOT EXISTS amount_repaid numeric(38,2),
    ADD COLUMN IF NOT EXISTS amount_outstanding numeric(38,2);

ALTER TABLE loan_entity
    ADD COLUMN IF NOT EXISTS loan_status VARCHAR(255);

ALTER TABLE loan_product
    ADD COLUMN IF NOT EXISTS investment_vehicle_id VARCHAR(255);

-- Create designation table after investment_vehicle_financier_entity columns are added
CREATE TABLE IF NOT EXISTS investment_vehicle_financier_entity_designation (
    investment_vehicle_financier_entity_id VARCHAR(255) NOT NULL,
    investment_vehicle_designation VARCHAR(255),
    CONSTRAINT fk_investment_vehicle_financier_entity FOREIGN KEY (investment_vehicle_financier_entity_id) REFERENCES investment_vehicle_financier_entity(id)
);

-- Cleanup invalid foreign key data (after all tables and columns exist)
UPDATE investment_vehicle_entity
SET leads_id = NULL
WHERE leads_id IS NOT NULL AND leads_id NOT IN (SELECT id FROM financier_entity);

UPDATE investment_vehicle_entity
SET contributors_id = NULL
WHERE contributors_id IS NOT NULL AND contributors_id NOT IN (SELECT id FROM financier_entity);

UPDATE meedl_user
SET bank_detail_entity_id = NULL
WHERE bank_detail_entity_id IS NOT NULL AND bank_detail_entity_id NOT IN (SELECT id FROM bank_detail_entity);

UPDATE meedl_user
SET next_of_kin_entity_id = NULL
WHERE next_of_kin_entity_id IS NOT NULL AND next_of_kin_entity_id NOT IN (SELECT id FROM next_of_kin_entity);

UPDATE investment_vehicle_financier_entity
SET financier_id = NULL
WHERE financier_id IS NOT NULL AND financier_id NOT IN (SELECT id FROM financier_entity);

UPDATE investment_vehicle_financier_entity
SET investment_vehicle_id = NULL
WHERE investment_vehicle_id IS NOT NULL AND investment_vehicle_id NOT IN (SELECT id FROM investment_vehicle_entity);

UPDATE financier_beneficial_owner_entity
SET financier_entity_id = NULL
WHERE financier_entity_id IS NOT NULL AND financier_entity_id NOT IN (SELECT id FROM financier_entity);

UPDATE financier_beneficial_owner_entity
SET beneficial_owner_entity_id = NULL
WHERE beneficial_owner_entity_id IS NOT NULL AND beneficial_owner_entity_id NOT IN (SELECT id FROM beneficial_owner_entity);

UPDATE financier_politically_exposed_person_entity
SET financier_id = NULL
WHERE financier_id IS NOT NULL AND financier_id NOT IN (SELECT id FROM financier_entity);

UPDATE financier_politically_exposed_person_entity
SET politically_exposed_person_id = NULL
WHERE politically_exposed_person_id IS NOT NULL AND politically_exposed_person_id NOT IN (SELECT id FROM politically_exposed_person_entity);

UPDATE vehicle_operation_entity
SET coupon_distribution_id = NULL
WHERE coupon_distribution_id IS NOT NULL AND coupon_distribution_id NOT IN (SELECT id FROM coupon_distribution_entity);

UPDATE vehicle_closure_entity
SET capital_distribution_id = NULL
WHERE capital_distribution_id IS NOT NULL AND capital_distribution_id NOT IN (SELECT id FROM capital_distribution_entity);

UPDATE meedl_notification_entity
SET meedl_user = NULL
WHERE meedl_user IS NOT NULL AND meedl_user NOT IN (SELECT id FROM meedl_user);

-- Add foreign key constraints (after all tables and columns exist)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_user_identity'
        AND table_name = 'financier_entity'
    ) THEN
        ALTER TABLE financier_entity
            ADD CONSTRAINT fk_user_identity
            FOREIGN KEY (user_identity_id) REFERENCES meedl_user(id);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_cooperation'
        AND table_name = 'financier_entity'
    ) THEN
        ALTER TABLE financier_entity
            ADD CONSTRAINT fk_cooperation
            FOREIGN KEY (cooperation_id) REFERENCES cooperation_entity(id);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_financier'
        AND table_name = 'financier_beneficial_owner_entity'
    ) THEN
        ALTER TABLE financier_beneficial_owner_entity
            ADD CONSTRAINT fk_financier
            FOREIGN KEY (financier_entity_id) REFERENCES financier_entity(id);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_beneficial_owner'
        AND table_name = 'financier_beneficial_owner_entity'
    ) THEN
        ALTER TABLE financier_beneficial_owner_entity
            ADD CONSTRAINT fk_beneficial_owner
            FOREIGN KEY (beneficial_owner_entity_id) REFERENCES beneficial_owner_entity(id);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_financier'
        AND table_name = 'financier_politically_exposed_person_entity'
    ) THEN
        ALTER TABLE financier_politically_exposed_person_entity
            ADD CONSTRAINT fk_financier
            FOREIGN KEY (financier_id) REFERENCES financier_entity(id);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_politically_exposed_person'
        AND table_name = 'financier_politically_exposed_person_entity'
    ) THEN
        ALTER TABLE financier_politically_exposed_person_entity
            ADD CONSTRAINT fk_politically_exposed_person
            FOREIGN KEY (politically_exposed_person_id) REFERENCES politically_exposed_person_entity(id);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_coupon_distribution'
        AND table_name = 'vehicle_operation_entity'
    ) THEN
        ALTER TABLE vehicle_operation_entity
            ADD CONSTRAINT fk_coupon_distribution
            FOREIGN KEY (coupon_distribution_id) REFERENCES coupon_distribution_entity(id);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_capital_distribution'
        AND table_name = 'vehicle_closure_entity'
    ) THEN
        ALTER TABLE vehicle_closure_entity
            ADD CONSTRAINT fk_capital_distribution
            FOREIGN KEY (capital_distribution_id) REFERENCES capital_distribution_entity(id);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_operation'
        AND table_name = 'investment_vehicle_entity'
    ) THEN
        ALTER TABLE investment_vehicle_entity
            ADD CONSTRAINT fk_operation
            FOREIGN KEY (operation_id) REFERENCES vehicle_operation_entity(id);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_closure'
        AND table_name = 'investment_vehicle_entity'
    ) THEN
        ALTER TABLE investment_vehicle_entity
            ADD CONSTRAINT fk_closure
            FOREIGN KEY (closure_id) REFERENCES vehicle_closure_entity(id);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_main_account'
        AND table_name = 'investment_vehicle_entity'
    ) THEN
        ALTER TABLE investment_vehicle_entity
            ADD CONSTRAINT fk_main_account
            FOREIGN KEY (main_account_id) REFERENCES bank_detail_entity(id);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_syncing_account'
        AND table_name = 'investment_vehicle_entity'
    ) THEN
        ALTER TABLE investment_vehicle_entity
            ADD CONSTRAINT fk_syncing_account
            FOREIGN KEY (syncing_account_id) REFERENCES bank_detail_entity(id);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_leads'
        AND table_name = 'investment_vehicle_entity'
    ) THEN
        ALTER TABLE investment_vehicle_entity
            ADD CONSTRAINT fk_leads
            FOREIGN KEY (leads_id) REFERENCES financier_entity(id);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_contributors'
        AND table_name = 'investment_vehicle_entity'
    ) THEN
        ALTER TABLE investment_vehicle_entity
            ADD CONSTRAINT fk_contributors
            FOREIGN KEY (contributors_id) REFERENCES financier_entity(id);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_bank_detail'
        AND table_name = 'meedl_user'
    ) THEN
        ALTER TABLE meedl_user
            ADD CONSTRAINT fk_bank_detail
            FOREIGN KEY (bank_detail_entity_id) REFERENCES bank_detail_entity(id);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_next_of_kin'
        AND table_name = 'meedl_user'
    ) THEN
        ALTER TABLE meedl_user
            ADD CONSTRAINT fk_next_of_kin
            FOREIGN KEY (next_of_kin_entity_id) REFERENCES next_of_kin_entity(id);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_financier'
        AND table_name = 'investment_vehicle_financier_entity'
    ) THEN
        ALTER TABLE investment_vehicle_financier_entity
            ADD CONSTRAINT fk_financier
            FOREIGN KEY (financier_id) REFERENCES financier_entity(id);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_investment_vehicle'
        AND table_name = 'investment_vehicle_financier_entity'
    ) THEN
        ALTER TABLE investment_vehicle_financier_entity
            ADD CONSTRAINT fk_investment_vehicle
            FOREIGN KEY (investment_vehicle_id) REFERENCES investment_vehicle_entity(id);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_meedl_user'
        AND table_name = 'meedl_notification_entity'
    ) THEN
        ALTER TABLE meedl_notification_entity
            ADD CONSTRAINT fk_meedl_user
            FOREIGN KEY (meedl_user) REFERENCES meedl_user(id);
    END IF;
END $$;

-- Modify loanee_response in loan_offer_entity
DO $$
DECLARE
    constraint_name TEXT;
BEGIN
    -- Drop any check constraints on loanee_response
    FOR constraint_name IN (
        SELECT conname
        FROM pg_constraint
        WHERE conrelid = 'loan_offer_entity'::regclass
        AND contype = 'c'
        AND pg_get_constraintdef(oid) LIKE '%loanee_response%'
    ) LOOP
        EXECUTE 'ALTER TABLE loan_offer_entity DROP CONSTRAINT ' || quote_ident(constraint_name);
    END LOOP;

    -- Disable triggers
    IF EXISTS (
        SELECT 1
        FROM information_schema.triggers
        WHERE event_object_table = 'loan_offer_entity'
    ) THEN
        ALTER TABLE loan_offer_entity DISABLE TRIGGER ALL;
    END IF;

    -- Modify column type
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'loan_offer_entity'
        AND column_name = 'loanee_response'
        AND data_type != 'character varying'
    ) THEN
        ALTER TABLE loan_offer_entity
            ALTER COLUMN loanee_response
            TYPE VARCHAR(255)
            USING (
                CASE
                    WHEN loanee_response::text = '0' THEN 'ACCEPTED'
                    WHEN loanee_response::text = '1' THEN 'DECLINED'
                    ELSE loanee_response::text
                END
            );

        -- Add a new check constraint
        ALTER TABLE loan_offer_entity
            ADD CONSTRAINT chk_loanee_response
            CHECK (loanee_response IN ('ACCEPTED', 'DECLINED'));
    END IF;

    -- Re-enable triggers
    IF EXISTS (
        SELECT 1
        FROM information_schema.triggers
        WHERE event_object_table = 'loan_offer_entity'
    ) THEN
        ALTER TABLE loan_offer_entity ENABLE TRIGGER ALL;
    END IF;
END $$;
