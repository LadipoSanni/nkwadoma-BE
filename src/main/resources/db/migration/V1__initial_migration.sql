CREATE TABLE IF NOT EXISTS black_listed_token
(
    expiration_date TIMESTAMP WITHOUT TIME ZONE,
    access_token    VARCHAR(2000) NOT NULL,
    CONSTRAINT black_listed_token_pkey PRIMARY KEY (access_token)
    );

CREATE TABLE IF NOT EXISTS cohort_entity
(
    expected_end_date         date,
    number_of_loanees         INTEGER,
    number_of_referred_loanee INTEGER,
    start_date                date,
    total_cohort_fee          numeric(38, 2),
    tuition_amount            numeric(38, 2),
    created_at                TIMESTAMP WITHOUT TIME ZONE,
    updated_at                TIMESTAMP WITHOUT TIME ZONE,
    cohort_description        VARCHAR(2500),
    activation_status         VARCHAR(255),
    cohort_status             VARCHAR(255),
    created_by                VARCHAR(255) NOT NULL,
    id                        VARCHAR(255) NOT NULL,
    image_url                 VARCHAR(255),
    loan_detail_id            VARCHAR(255),
    name                      VARCHAR(255),
    organization_id           VARCHAR(255),
    program_id                VARCHAR(255),
    updated_by                VARCHAR(255),
    CONSTRAINT cohort_entity_pkey PRIMARY KEY (id)
    );


CREATE TABLE IF NOT EXISTS curriculum_entity
(
    description       VARCHAR(255),
    duration          VARCHAR(255),
    id                VARCHAR(255) NOT NULL,
    name              VARCHAR(255),
    program_entity_id VARCHAR(255),
    objectives        OID,
    CONSTRAINT curriculum_entity_pkey PRIMARY KEY (id)
    );


CREATE TABLE IF NOT EXISTS flyway_schema_history
(
    installed_rank INTEGER                                   NOT NULL,
    version        VARCHAR(50),
    description    VARCHAR(200)                              NOT NULL,
    type           VARCHAR(20)                               NOT NULL,
    script         VARCHAR(1000)                             NOT NULL,
    checksum       INTEGER,
    installed_by   VARCHAR(100)                              NOT NULL,
    installed_on   TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW() NOT NULL,
    execution_time INTEGER                                   NOT NULL,
    success        BOOLEAN                                   NOT NULL,
    CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank)
    );

CREATE TABLE IF NOT EXISTS identity_verification_entity
(
    status      SMALLINT,
    bvn         VARCHAR(255),
    email       VARCHAR(255),
    id          VARCHAR(255) NOT NULL,
    nin         VARCHAR(255),
    referral_id VARCHAR(255),
    CONSTRAINT identity_verification_entity_pkey PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS identity_verification_failure_record_entity
(
    email            VARCHAR(255),
    id               VARCHAR(255) NOT NULL,
    reason           VARCHAR(255),
    referral_id      VARCHAR(255),
    service_provider VARCHAR(255),
    CONSTRAINT identity_verification_failure_record_entity_pkey PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS investment_vehicle_entity
(
    minimum_investment_amount numeric(38, 2),
    rate                      FLOAT,
    size                      numeric(38, 2),
    start_date                date,
    tenure                    INTEGER      NOT NULL,
    total_available_amount    numeric(38, 2),
    mandate                   VARCHAR(2500),
    bank_partner              VARCHAR(255),
    contributors_id           VARCHAR(255),
    custodian                 VARCHAR(255),
    fund_manager              VARCHAR(255),
    fund_raising_status       VARCHAR(255),
    id                        VARCHAR(255) NOT NULL,
    investment_vehicle_type   VARCHAR(255),
    leads_id                  VARCHAR(255),
    name                      VARCHAR(255),
    sponsors                  VARCHAR(255),
    trustee                   VARCHAR(255),
    CONSTRAINT investment_vehicle_entity_pkey PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS investment_vehicle_financier_entity
(
    investment_vehicle_role SMALLINT,
    id                      VARCHAR(255) NOT NULL,
    CONSTRAINT investment_vehicle_financier_entity_pkey PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS investment_vehicle_financier_entity_individuals
(
    individuals_id                         VARCHAR(255) NOT NULL,
    investment_vehicle_financier_entity_id VARCHAR(255) NOT NULL
    );


CREATE TABLE IF NOT EXISTS investment_vehicle_financier_entity_organizations
(
    investment_vehicle_financier_entity_id VARCHAR(255) NOT NULL,
    organizations_id                       VARCHAR(255) NOT NULL
    );


CREATE TABLE IF NOT EXISTS loan_breakdown_entity
(
    item_amount       numeric(38, 2),
    cohort_id         VARCHAR(255),
    currency          VARCHAR(255),
    item_name         VARCHAR(255),
    loan_breakdown_id VARCHAR(255) NOT NULL,
    CONSTRAINT loan_breakdown_entity_pkey PRIMARY KEY (loan_breakdown_id)
    );

CREATE TABLE IF NOT EXISTS loan_detail_entity
(
    debt_percentage         DOUBLE PRECISION,
    last_month_actual       numeric(38, 2),
    monthly_expected        numeric(38, 2),
    repayment_percentage    DOUBLE PRECISION,
    total_amount_disbursed  numeric(38, 2),
    total_amount_repaid     numeric(38, 2),
    total_interest_incurred numeric(38, 2),
    total_outstanding       numeric(38, 2),
    id                      VARCHAR(255) NOT NULL,
    CONSTRAINT loan_detail_entity_pkey PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS loan_entity
(
    last_updated_date TIMESTAMP WITHOUT TIME ZONE,
    start_date        TIMESTAMP WITHOUT TIME ZONE,
    id                VARCHAR(255) NOT NULL,
    loan_account_id   VARCHAR(255),
    loan_offer_id     VARCHAR(255),
    loanee_entity_id  VARCHAR(255),
    CONSTRAINT loan_entity_pkey PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS loan_metrics_entity
(
    loan_disbursal_count INTEGER      NOT NULL,
    loan_offer_count     INTEGER      NOT NULL,
    loan_referral_count  INTEGER      NOT NULL,
    loan_request_count   INTEGER      NOT NULL,
    id                   VARCHAR(255) NOT NULL,
    organization_id      VARCHAR(255),
    CONSTRAINT loan_metrics_entity_pkey PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS loan_offer_entity
(
    amount_approved    numeric(38, 2),
    loanee_response    SMALLINT,
    date_time_accepted TIMESTAMP WITHOUT TIME ZONE,
    date_time_offered  TIMESTAMP WITHOUT TIME ZONE,
    id                 VARCHAR(255) NOT NULL,
    loan_offer_status  VARCHAR(255),
    loan_product_id    VARCHAR(255),
    loan_request_id    VARCHAR(255),
    loanee_id          VARCHAR(255),
    CONSTRAINT loan_offer_entity_pkey PRIMARY KEY (id)
    );


CREATE TABLE IF NOT EXISTS loan_product
(
    cost_of_fund                 DOUBLE PRECISION  NOT NULL,
    interest_rate                DOUBLE PRECISION  NOT NULL,
    loan_product_size            numeric(38, 2),
    min_repayment_amount         numeric(38, 2),
    moratorium                   INTEGER           NOT NULL,
    obligor_loan_limit           numeric(38, 2),
    tenor                        INTEGER           NOT NULL,
    total_amount_available       numeric(38, 2),
    total_amount_disbursed       numeric(38, 2),
    total_amount_earned          numeric(38, 2),
    total_amount_repaid          numeric(38, 2),
    total_number_of_loan_product INTEGER           NOT NULL,
    total_number_of_loanees      INTEGER DEFAULT 0 NOT NULL,
    created_at                   TIMESTAMP WITHOUT TIME ZONE,
    updated_at                   TIMESTAMP WITHOUT TIME ZONE,
    mandate                      VARCHAR(5500),
    terms_and_condition          VARCHAR(15000),
    bank_partner                 VARCHAR(255),
    disbursement_terms           VARCHAR(255),
    id                           VARCHAR(255)      NOT NULL,
    investment_vehicle_name      VARCHAR(255),
    loan_product_status          VARCHAR(255),
    name                         VARCHAR(255),
    sponsor                      VARCHAR(255),
    CONSTRAINT loan_product_pkey PRIMARY KEY (id),
    CONSTRAINT loan_product_name_key UNIQUE (name)
    );


CREATE TABLE IF NOT EXISTS loan_product_entity_sponsors
(
    loan_product_entity_id VARCHAR(255) NOT NULL,
    sponsors               VARCHAR(255)
    );

CREATE TABLE IF NOT EXISTS loan_product_vendor
(
    id                     VARCHAR(255) NOT NULL,
    loan_product_entity_id VARCHAR(255),
    vendor_entity_id       VARCHAR(255),
    CONSTRAINT loan_product_vendor_pkey PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS loan_referral_entity
(
    id                   VARCHAR(255) NOT NULL,
    loan_referral_status VARCHAR(255),
    loanee_entity_id     VARCHAR(255),
    reason_for_declining VARCHAR(255),
    CONSTRAINT loan_referral_entity_pkey PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS loan_request_entity
(
    loan_amount_approved              numeric(38, 2),
    loan_amount_requested             numeric(38, 2),
    loan_request_decision             SMALLINT,
    created_date                      TIMESTAMP WITHOUT TIME ZONE,
    date_time_approved                TIMESTAMP WITHOUT TIME ZONE,
    cohort_id                         VARCHAR(255),
    decline_reason                    VARCHAR(255),
    id                                VARCHAR(255) NOT NULL,
    loan_referral_id                  VARCHAR(255),
    loanee_entity_id                  VARCHAR(255),
    reason_for_declining_loan_request VARCHAR(255),
    referred_by                       VARCHAR(255),
    status                            VARCHAR(255),
    CONSTRAINT loan_request_entity_pkey PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS loanee_entity
(
    credit_score            INTEGER      NOT NULL,
    created_at              TIMESTAMP WITHOUT TIME ZONE,
    credit_score_updated_at TIMESTAMP WITHOUT TIME ZONE,
    referral_date_time      TIMESTAMP WITHOUT TIME ZONE,
    updated_at              TIMESTAMP WITHOUT TIME ZONE,
    cohort_id               VARCHAR(255),
    created_by              VARCHAR(255),
    id                      VARCHAR(255) NOT NULL,
    loanee_loan_detail_id   VARCHAR(255),
    loanee_status           VARCHAR(255),
    referred_by             VARCHAR(255),
    registry_id             VARCHAR(255),
    user_identity_id        VARCHAR(255),
    CONSTRAINT loanee_entity_pkey PRIMARY KEY (id)
    );


CREATE TABLE IF NOT EXISTS loanee_loan_account_entity
(
    account_status VARCHAR(255),
    id             VARCHAR(255) NOT NULL,
    loan_status    VARCHAR(255),
    loanee_id      VARCHAR(255),
    CONSTRAINT loanee_loan_account_entity_pkey PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS loanee_loan_breakdown_entity
(
    item_amount              numeric(38, 2),
    currency                 VARCHAR(255),
    item_name                VARCHAR(255),
    loanee_id                VARCHAR(255),
    loanee_loan_breakdown_id VARCHAR(255) NOT NULL,
    CONSTRAINT loanee_loan_breakdown_entity_pkey PRIMARY KEY (loanee_loan_breakdown_id)
    );

CREATE TABLE IF NOT EXISTS loanee_loan_detail_entity
(
    amount_requested numeric(38, 2),
    initial_deposit  numeric(38, 2),
    tuition_amount   numeric(38, 2),
    id               VARCHAR(255) NOT NULL,
    CONSTRAINT loanee_loan_detail_entity_pkey PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS meedl_user
(
    email_verified            BOOLEAN      NOT NULL,
    enabled                   BOOLEAN      NOT NULL,
    is_identity_verified      BOOLEAN DEFAULT FALSE,
    alternate_contact_address VARCHAR(255),
    alternate_email           VARCHAR(255),
    alternate_phone_number    VARCHAR(255),
    bvn                       VARCHAR(255),
    created_at                VARCHAR(255),
    created_by                VARCHAR(255),
    date_of_birth             VARCHAR(255),
    deactivation_reason       VARCHAR(255),
    email                     VARCHAR(255),
    first_name                VARCHAR(255),
    gender                    VARCHAR(255),
    id                        VARCHAR(255) NOT NULL,
    image                     VARCHAR(255),
    last_name                 VARCHAR(255),
    lga_of_origin             VARCHAR(255),
    lga_of_residence          VARCHAR(255),
    marital_status            VARCHAR(255),
    middle_name               VARCHAR(255),
    nationality               VARCHAR(255),
    nin                       VARCHAR(255),
    phone_number              VARCHAR(255),
    reactivation_reason       VARCHAR(255),
    residential_address       VARCHAR(255),
    role                      VARCHAR(255),
    state                     VARCHAR(255),
    state_of_origin           VARCHAR(255),
    state_of_residence        VARCHAR(255),
    CONSTRAINT meedl_user_pkey PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS next_of_kin_entity
(
    contact_address          VARCHAR(255),
    email                    VARCHAR(255),
    first_name               VARCHAR(255),
    id                       VARCHAR(255) NOT NULL,
    last_name                VARCHAR(255),
    loanee_entity_id         VARCHAR(255),
    next_of_kin_relationship VARCHAR(255),
    phone_number             VARCHAR(255),
    CONSTRAINT next_of_kin_entity_pkey PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS organization
(
    is_enabled          BOOLEAN           NOT NULL,
    number_of_cohort    INTEGER           NOT NULL,
    number_of_loanees   INTEGER           NOT NULL,
    number_of_programs  INTEGER DEFAULT 0 NOT NULL,
    invited_date        TIMESTAMP WITHOUT TIME ZONE,
    time_updated        TIMESTAMP WITHOUT TIME ZONE,
    address             VARCHAR(255),
    banner_image        VARCHAR(255),
    created_by          VARCHAR(255),
    email               VARCHAR(255)      NOT NULL,
    id                  VARCHAR(255)      NOT NULL,
    logo_image          VARCHAR(255),
    name                VARCHAR(255)      NOT NULL,
    office_address      VARCHAR(255),
    phone_number        VARCHAR(255),
    rc_number           VARCHAR(255),
    registration_number VARCHAR(255),
    status              VARCHAR(255),
    tax_identity        VARCHAR(255),
    updated_by          VARCHAR(255),
    website_address     VARCHAR(255),
    CONSTRAINT organization_pkey PRIMARY KEY (id)
    );


CREATE TABLE IF NOT EXISTS organization_employee
(
    id            VARCHAR(255) NOT NULL,
    meedl_user_id VARCHAR(255),
    organization  VARCHAR(255),
    status        VARCHAR(255),
    CONSTRAINT organization_employee_pkey PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS organization_service_offering_entity
(
    id                         VARCHAR(255) NOT NULL,
    organization_id            VARCHAR(255),
    service_offering_entity_id VARCHAR(255),
    CONSTRAINT organization_service_offering_entity_pkey PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS program_cohort_entity
(
    cohort_id  VARCHAR(255),
    id         VARCHAR(255) NOT NULL,
    program_id VARCHAR(255),
    CONSTRAINT program_cohort_entity_pkey PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS program_entity
(
    duration                 INTEGER      NOT NULL,
    number_of_cohort         INTEGER      NOT NULL,
    number_of_loanees        INTEGER      NOT NULL,
    program_start_date       date,
    created_at               TIMESTAMP WITHOUT TIME ZONE,
    updated_at               TIMESTAMP WITHOUT TIME ZONE,
    program_description      VARCHAR(2500),
    created_by               VARCHAR(255) NOT NULL,
    delivery_type            VARCHAR(255),
    duration_type            VARCHAR(255),
    id                       VARCHAR(255) NOT NULL,
    mode                     VARCHAR(255),
    name                     VARCHAR(255),
    organization_identity_id VARCHAR(255),
    program_status           VARCHAR(255),
    updated_by               VARCHAR(255),
    objectives               OID,
    CONSTRAINT program_entity_pkey PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS service_offering_entity
(
    transaction_lower_bound numeric(38, 2),
    transaction_upper_bound numeric(38, 2),
    id                      VARCHAR(255) NOT NULL,
    industry                VARCHAR(255),
    name                    VARCHAR(255),
    CONSTRAINT service_offering_entity_pkey PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS subject_entity
(
    curriculum_entity_id VARCHAR(255),
    description          VARCHAR(255),
    id                   VARCHAR(255) NOT NULL,
    name                 VARCHAR(255),
    CONSTRAINT subject_entity_pkey PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS vendor_entity
(
    product              SMALLINT,
    id                   VARCHAR(255) NOT NULL,
    terms_and_conditions VARCHAR(255),
    vendor_name          VARCHAR(255),
    CONSTRAINT vendor_entity_pkey PRIMARY KEY (id)
    );