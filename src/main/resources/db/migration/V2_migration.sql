-- Create bank_detail_entity
CREATE TABLE IF NOT EXISTS bank_detail_entity (
                                                  id VARCHAR(255) NOT NULL,
    bank_name VARCHAR(255),
    bank_number VARCHAR(255),
    CONSTRAINT bank_detail_entity_pkey PRIMARY KEY (id)
    );

-- Create cooperation_entity
CREATE TABLE IF NOT EXISTS cooperation_entity (
                                                  id VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    CONSTRAINT cooperation_entity_pkey PRIMARY KEY (id)
    );

-- Add missing columns to loanee_entity
ALTER TABLE loanee_entity
    ADD COLUMN deferral_approved BOOLEAN DEFAULT FALSE,
    ADD COLUMN defer_reason VARCHAR(255),
    ADD COLUMN deferral_requested BOOLEAN DEFAULT FALSE,
    ADD COLUMN deferred_date_and_time TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN dropout_approved BOOLEAN DEFAULT FALSE,
    ADD COLUMN dropout_requested BOOLEAN DEFAULT FALSE,
    ADD COLUMN reason_for_dropout VARCHAR(255),
    ADD COLUMN onboarding_mode VARCHAR(255),
    ADD COLUMN uploaded_status VARCHAR(225);

-- Create financier_entity and source_of_funds
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
    CONSTRAINT financier_entity_pkey PRIMARY KEY (id),
    CONSTRAINT fk_user_identity FOREIGN KEY (user_identity_id) REFERENCES meedl_user(id),
    CONSTRAINT fk_cooperation FOREIGN KEY (cooperation_id) REFERENCES cooperation_entity(id)
    );

CREATE TABLE IF NOT EXISTS financier_entity_source_of_funds (
                                                                financier_entity_id VARCHAR(255) NOT NULL,
    source_of_funds VARCHAR(255),
    CONSTRAINT fk_financier_entity FOREIGN KEY (financier_entity_id) REFERENCES financier_entity(id)
    );

-- Create beneficial_owner_entity
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

-- Create financier_beneficial_owner_entity
CREATE TABLE IF NOT EXISTS financier_beneficial_owner_entity (
                                                                 id VARCHAR(255) NOT NULL,
    financier_entity_id VARCHAR(255),
    beneficial_owner_entity_id VARCHAR(255),
    CONSTRAINT financier_beneficial_owner_entity_pkey PRIMARY KEY (id),
    CONSTRAINT fk_financier FOREIGN KEY (financier_entity_id) REFERENCES financier_entity(id),
    CONSTRAINT fk_beneficial_owner FOREIGN KEY (beneficial_owner_entity_id) REFERENCES beneficial_owner_entity(id)
    );

-- Create politically_exposed_person_entity
CREATE TABLE IF NOT EXISTS politically_exposed_person_entity (
                                                                 id VARCHAR(255) NOT NULL,
    position_held VARCHAR(255),
    country VARCHAR(255),
    relationship VARCHAR(255),
    additional_information VARCHAR(255),
    CONSTRAINT politically_exposed_person_entity_pkey PRIMARY KEY (id)
    );

-- Create financier_politically_exposed_person_entity
CREATE TABLE IF NOT EXISTS financier_politically_exposed_person_entity (
                                                                           id VARCHAR(255) NOT NULL,
    financier_id VARCHAR(255),
    politically_exposed_person_id VARCHAR(255),
    CONSTRAINT financier_politically_exposed_person_entity_pkey PRIMARY KEY (id),
    CONSTRAINT fk_financier FOREIGN KEY (financier_id) REFERENCES financier_entity(id),
    CONSTRAINT fk_politically_exposed_person FOREIGN KEY (politically_exposed_person_id) REFERENCES politically_exposed_person_entity(id)
    );

-- Create coupon_distribution_entity
CREATE TABLE IF NOT EXISTS coupon_distribution_entity (
                                                          id VARCHAR(255) NOT NULL,
    due INTEGER,
    paid INTEGER,
    last_date_paid TIMESTAMP WITHOUT TIME ZONE,
    last_date_due TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT coupon_distribution_entity_pkey PRIMARY KEY (id)
    );

-- Create capital_distribution_entity
CREATE TABLE IF NOT EXISTS capital_distribution_entity (
                                                           id VARCHAR(255) NOT NULL,
    due INTEGER,
    total_capital_paid_out numeric(38,2),
    CONSTRAINT capital_distribution_entity_pkey PRIMARY KEY (id)
    );

-- Create vehicle_operation_entity
CREATE TABLE IF NOT EXISTS vehicle_operation_entity (
                                                        id VARCHAR(255) NOT NULL,
    coupon_distribution_status VARCHAR(255),
    coupon_distribution_id VARCHAR(255),
    fund_raising_status VARCHAR(255),
    deploying_status VARCHAR(255),
    operation_status VARCHAR(255),
    CONSTRAINT vehicle_operation_entity_pkey PRIMARY KEY (id),
    CONSTRAINT fk_coupon_distribution FOREIGN KEY (coupon_distribution_id) REFERENCES coupon_distribution_entity(id)
    );

-- Create vehicle_closure_entity
CREATE TABLE IF NOT EXISTS vehicle_closure_entity (
                                                      id VARCHAR(255) NOT NULL,
    recollection_status VARCHAR(255),
    capital_distribution_id VARCHAR(255),
    maturity VARCHAR(255),
    CONSTRAINT vehicle_closure_entity_pkey PRIMARY KEY (id),
    CONSTRAINT fk_capital_distribution FOREIGN KEY (capital_distribution_id) REFERENCES capital_distribution_entity(id)
    );

-- Add missing columns to investment_vehicle_entity
ALTER TABLE investment_vehicle_entity
    ADD COLUMN created_date TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN investment_vehicle_status VARCHAR(255),
    ADD COLUMN investment_vehicle_link VARCHAR(255),
    ADD COLUMN operation_id VARCHAR(255),
    ADD COLUMN closure_id VARCHAR(255),
    ADD COLUMN main_account_id VARCHAR(255),
    ADD COLUMN syncing_account_id VARCHAR(255),
    ADD COLUMN last_updated_date TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN investment_vehicle_visibility VARCHAR(255),
    ADD COLUMN talent_funded INTEGER DEFAULT 0,
    ADD CONSTRAINT fk_operation FOREIGN KEY (operation_id) REFERENCES vehicle_operation_entity(id),
    ADD CONSTRAINT fk_closure FOREIGN KEY (closure_id) REFERENCES vehicle_closure_entity(id),
    ADD CONSTRAINT fk_main_account FOREIGN KEY (main_account_id) REFERENCES bank_detail_entity(id),
    ADD CONSTRAINT fk_syncing_account FOREIGN KEY (syncing_account_id) REFERENCES bank_detail_entity(id),
    ADD CONSTRAINT fk_leads FOREIGN KEY (leads_id) REFERENCES financier_entity(id),
    ADD CONSTRAINT fk_contributors FOREIGN KEY (contributors_id) REFERENCES financier_entity(id);

-- Create meedl_notification_entity
CREATE TABLE IF NOT EXISTS meedl_notification_entity (
                                                         id VARCHAR(255) NOT NULL,
    title VARCHAR(255),
    content_id VARCHAR(255),
    meedl_user_id VARCHAR(255) NOT NULL,
    read BOOLEAN,
    timestamp TIMESTAMP WITHOUT TIME ZONE,
    call_to_action BOOLEAN,
    sender_mail VARCHAR(255),
    sender_full_name VARCHAR(255),
    content_detail VARCHAR(255),
    notification_flag VARCHAR(255),
    CONSTRAINT meedl_notification_entity_pkey PRIMARY KEY (id),
    CONSTRAINT fk_meedl_user FOREIGN KEY (meedl_user_id) REFERENCES meedl_user(id)
    );

-- Create portfolio_entity
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

-- Create repayment_history_entity
CREATE TABLE IF NOT EXISTS repayment_history_entity (
                                                        id VARCHAR(255) NOT NULL,
    loanee_id VARCHAR(255),
    payment_date_time TIMESTAMP WITHOUT TIME ZONE,
    cohort_id VARCHAR(255),
    amount_paid numeric(38,2),
    total_amount_repaid numeric(38,2),
    amount_outstanding numeric(38,2),
    mode_of_payment VARCHAR(255),
    CONSTRAINT repayment_history_entity_pkey PRIMARY KEY (id),
    CONSTRAINT fk_loanee FOREIGN KEY (loanee_id) REFERENCES loanee_entity(id),
    CONSTRAINT fk_cohort FOREIGN KEY (cohort_id) REFERENCES cohort_entity(id)
    );

-- Add missing columns to meedl_user
ALTER TABLE meedl_user
    ADD COLUMN tax_id VARCHAR(255),
    ADD COLUMN bank_detail_entity_id VARCHAR(255),
    ADD COLUMN next_of_kin_entity_id VARCHAR(255),
    ADD COLUMN address VARCHAR(255),
    ADD CONSTRAINT fk_bank_detail FOREIGN KEY (bank_detail_entity_id) REFERENCES bank_detail_entity(id),
    ADD CONSTRAINT fk_next_of_kin FOREIGN KEY (next_of_kin_entity_id) REFERENCES next_of_kin_entity(id);

-- Add investment_vehicle_id to loan_product
ALTER TABLE loan_product
    ADD COLUMN investment_vehicle_id VARCHAR(255);

-- Add missing columns to investment_vehicle_financier_entity
ALTER TABLE investment_vehicle_financier_entity
    ADD COLUMN amount_invested numeric(38,2),
    ADD COLUMN financier_id VARCHAR(255),
    ADD COLUMN investment_vehicle_id VARCHAR(255),
    ADD COLUMN date_invested date,
    ADD CONSTRAINT fk_financier FOREIGN KEY (financier_id) REFERENCES financier_entity(id),
    ADD CONSTRAINT fk_investment_vehicle FOREIGN KEY (investment_vehicle_id) REFERENCES investment_vehicle_entity(id);

CREATE TABLE IF NOT EXISTS investment_vehicle_financier_entity_designation (
                                                                               investment_vehicle_financier_entity_id VARCHAR(255) NOT NULL,
    investment_vehicle_designation VARCHAR(255),
    CONSTRAINT fk_investment_vehicle_financier_entity FOREIGN KEY (investment_vehicle_financier_entity_id) REFERENCES investment_vehicle_financier_entity(id)
    );

-- Add missing columns to loanee_loan_detail_entity
ALTER TABLE loanee_loan_detail_entity
    ADD COLUMN amount_received numeric(38,2),
    ADD COLUMN amount_repaid numeric(38,2),
    ADD COLUMN amount_outstanding numeric(38,2);

-- Add loan_status to loan_entity
ALTER TABLE loan_entity
    ADD COLUMN loan_status VARCHAR(255);

-- Modify loanee_response in loan_offer_entity
ALTER TABLE loan_offer_entity
ALTER COLUMN loanee_response TYPE VARCHAR(255);