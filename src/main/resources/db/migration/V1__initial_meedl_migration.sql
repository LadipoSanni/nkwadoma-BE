CREATE TABLE IF NOT EXISTS black_listed_token
(
    access_token VARCHAR(2000) NOT NULL,
    CONSTRAINT pk_blacklistedtoken PRIMARY KEY (access_token)
);

CREATE TABLE IF NOT EXISTS cohort_entity
(
    id                        VARCHAR(255) NOT NULL,
    name                      VARCHAR(255),
    program_id                VARCHAR(255),
    organization_id           VARCHAR(255),
    cohort_description        VARCHAR(2500),
    activation_status         VARCHAR(255),
    cohort_status             VARCHAR(255),
    created_at                TIMESTAMP WITHOUT TIME ZONE,
    updated_at                TIMESTAMP WITHOUT TIME ZONE,
    tuition_amount            DECIMAL,
    total_cohort_fee          DECIMAL,
    created_by                VARCHAR(255) NOT NULL,
    updated_by                VARCHAR(255),
    image_url                 VARCHAR(255),
    start_date                date,
    expected_end_date         date,
    loan_detail_id            VARCHAR(255),
    number_of_loanees         INTEGER,
    number_of_referred_loanee INTEGER,
    CONSTRAINT pk_cohortentity PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS curriculum_entity
(
    id                VARCHAR(255) NOT NULL,
    description       VARCHAR(255),
    objectives        OID,
    duration          VARCHAR(255),
    name              VARCHAR(255),
    program_entity_id VARCHAR(255),
    CONSTRAINT pk_curriculum_entity PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS identity_verification_entity
(
    id          VARCHAR(255) NOT NULL,
    bvn         VARCHAR(255),
    nin         VARCHAR(255),
    referral_id VARCHAR(255),
    email       VARCHAR(255),
    status      SMALLINT,
    CONSTRAINT pk_identityverificationentity PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS identity_verification_failure_record_entity
(
    id               VARCHAR(255) NOT NULL,
    email            VARCHAR(255),
    reason           VARCHAR(255),
    referral_id      VARCHAR(255),
    service_provider SMALLINT,
    CONSTRAINT pk_identityverificationfailurerecordentity PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS investment_vehicle_entity
(
    id                      VARCHAR(255) NOT NULL,
    name                    VARCHAR(255),
    investment_vehicle_type VARCHAR(255),
    mandate                 VARCHAR(255),
    sponsors                VARCHAR(255),
    tenure                  INTEGER      NOT NULL,
    size                    DECIMAL,
    rate                    FLOAT,
    fund_raising_status     VARCHAR(255),
    leads_id                VARCHAR(255),
    contributors_id         VARCHAR(255),
    CONSTRAINT pk_investmentvehicleentity PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS investment_vehicle_financier_entity
(
    id                      VARCHAR(255) NOT NULL,
    investment_vehicle_role SMALLINT,
    CONSTRAINT pk_investmentvehiclefinancierentity PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS investment_vehicle_financier_entity_individuals
(
    investment_vehicle_financier_entity_id VARCHAR(255) NOT NULL,
    individuals_id                         VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS investment_vehicle_financier_entity_organizations
(
    investment_vehicle_financier_entity_id VARCHAR(255) NOT NULL,
    organizations_id                       VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS loan_breakdown_entity
(
    loan_breakdown_id VARCHAR(255) NOT NULL,
    item_name         VARCHAR(255),
    item_amount       DECIMAL,
    currency          VARCHAR(255),
    cohort_id         VARCHAR(255),
    CONSTRAINT pk_loanbreakdownentity PRIMARY KEY (loan_breakdown_id)
);

CREATE TABLE IF NOT EXISTS loan_detail_entity
(
    id                      VARCHAR(255) NOT NULL,
    total_amount_disbursed  DECIMAL,
    total_amount_repaid     DECIMAL,
    total_outstanding       DECIMAL,
    repayment_percentage    DOUBLE PRECISION,
    debt_percentage         DOUBLE PRECISION,
    total_interest_incurred DECIMAL,
    monthly_expected        DECIMAL,
    last_month_actual       DECIMAL,
    CONSTRAINT pk_loandetailentity PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS loan_entity
(
    id                VARCHAR(255) NOT NULL,
    loanee_entity_id  VARCHAR(255),
    loan_account_id   VARCHAR(255),
    start_date        TIMESTAMP WITHOUT TIME ZONE,
    last_updated_date TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_loanentity PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS loan_offer_entitiy
(
    id                VARCHAR(255) NOT NULL,
    loan_offer_status VARCHAR(255),
    loan_request_id   VARCHAR(255),
    loan_product_id   VARCHAR(255),
    loanee_id         VARCHAR(255),
    date_time_offered TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_loanofferentitiy PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS loan_product
(
    id                           VARCHAR(255)      NOT NULL,
    name                         VARCHAR(255),
    moratorium                   INTEGER           NOT NULL,
    loan_product_status          VARCHAR(255),
    tenor                        INTEGER           NOT NULL,
    interest_rate                DOUBLE PRECISION  NOT NULL,
    cost_of_fund                 DOUBLE PRECISION  NOT NULL,
    terms_and_condition          VARCHAR(15000),
    obligor_loan_limit           DECIMAL,
    loan_product_size            DECIMAL,
    total_amount_available       DECIMAL,
    created_at                   TIMESTAMP WITHOUT TIME ZONE,
    updated_at                   TIMESTAMP WITHOUT TIME ZONE,
    total_amount_earned          DECIMAL,
    total_amount_disbursed       DECIMAL,
    total_amount_repaid          DECIMAL,
    mandate                      VARCHAR(5500),
    min_repayment_amount         DECIMAL,
    bank_partner                 VARCHAR(255),
    disbursement_terms           VARCHAR(255),
    fund_product_id              VARCHAR(255),
    total_number_of_loanees      INTEGER DEFAULT 0 NOT NULL,
    total_number_of_loan_product INTEGER           NOT NULL,
    CONSTRAINT pk_loan_product PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS loan_product_entity_sponsors
(
    loan_product_entity_id VARCHAR(255) NOT NULL,
    sponsors               VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS loan_product_vendor
(
    id                     VARCHAR(255) NOT NULL,
    vendor_entity_id       VARCHAR(255),
    loan_product_entity_id VARCHAR(255),
    CONSTRAINT pk_loanproductvendor PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS loan_referral_entity
(
    id                   VARCHAR(255) NOT NULL,
    loanee_entity_id     VARCHAR(255),
    loan_referral_status VARCHAR(255),
    CONSTRAINT pk_loanreferralentity PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS loan_request_entity
(
    id                                VARCHAR(255) NOT NULL,
    referred_by                       VARCHAR(255),
    cohort_id                         VARCHAR(255),
    loan_amount_requested             DECIMAL,
    loan_amount_approved              DECIMAL,
    loan_request_decision             SMALLINT,
    decline_reason                    VARCHAR(255),
    date_time_approved                TIMESTAMP WITHOUT TIME ZONE,
    created_date                      TIMESTAMP WITHOUT TIME ZONE,
    reason_for_declining_loan_request VARCHAR(255),
    status                            VARCHAR(255),
    loan_referral_id                  VARCHAR(255),
    loanee_entity_id                  VARCHAR(255),
    CONSTRAINT pk_loanrequestentity PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS loanee_entity
(
    id                    VARCHAR(255) NOT NULL,
    cohort_id             VARCHAR(255),
    created_by            VARCHAR(255),
    created_at            TIMESTAMP WITHOUT TIME ZONE,
    updated_at            TIMESTAMP WITHOUT TIME ZONE,
    user_identity_id      VARCHAR(255),
    loanee_loan_detail_id VARCHAR(255),
    loanee_status         VARCHAR(255),
    referral_date_time    TIMESTAMP WITHOUT TIME ZONE,
    referred_by           VARCHAR(255),
    full_name             VARCHAR(255),
    CONSTRAINT pk_loaneeentity PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS loanee_loan_breakdown_entity
(
    loanee_loan_breakdown_id VARCHAR(255) NOT NULL,
    item_name                VARCHAR(255),
    item_amount              DECIMAL,
    currency                 VARCHAR(255),
    loanee_id                VARCHAR(255),
    CONSTRAINT pk_loaneeloanbreakdownentity PRIMARY KEY (loanee_loan_breakdown_id)
);

CREATE TABLE IF NOT EXISTS loanee_loan_detail_entity
(
    id               VARCHAR(255) NOT NULL,
    tuition_amount   DECIMAL,
    initial_deposit  DECIMAL,
    amount_requested DECIMAL,
    CONSTRAINT pk_loaneeloandetailentity PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS meedl_user
(
    id                        VARCHAR(255) NOT NULL,
    email                     VARCHAR(255),
    first_name                VARCHAR(255),
    last_name                 VARCHAR(255),
    image                     VARCHAR(255),
    phone_number              VARCHAR(255),
    is_identity_verified      BOOLEAN      NOT NULL,
    email_verified            BOOLEAN      NOT NULL,
    enabled                   BOOLEAN      NOT NULL,
    created_at                VARCHAR(255),
    role                      VARCHAR(255),
    alternate_email           VARCHAR(255),
    alternate_phone_number    VARCHAR(255),
    alternate_contact_address VARCHAR(255),
    created_by                VARCHAR(255),
    reactivation_reason       VARCHAR(255),
    deactivation_reason       VARCHAR(255),
    bvn                       VARCHAR(255),
    nin                       VARCHAR(255),
    CONSTRAINT pk_meedl_user PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS next_of_kin_entity
(
    id                       VARCHAR(255) NOT NULL,
    first_name               VARCHAR(255),
    last_name                VARCHAR(255),
    email                    VARCHAR(255),
    phone_number             VARCHAR(255),
    next_of_kin_relationship VARCHAR(255),
    contact_address          VARCHAR(255),
    loanee_entity_id         VARCHAR(255),
    CONSTRAINT pk_nextofkinentity PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS organization
(
    id                  VARCHAR(255)      NOT NULL,
    name                VARCHAR(255),
    email               VARCHAR(255),
    website_address     VARCHAR(255),
    invited_date        TIMESTAMP WITHOUT TIME ZONE,
    registration_number VARCHAR(255),
    tax_identity        VARCHAR(255),
    phone_number        VARCHAR(255),
    status              VARCHAR(255),
    rc_number           VARCHAR(255),
    created_by          VARCHAR(255),
    updated_by          VARCHAR(255),
    time_updated        TIMESTAMP WITHOUT TIME ZONE,
    is_enabled          BOOLEAN           NOT NULL,
    number_of_programs  INTEGER DEFAULT 0 NOT NULL,
    logo_image          VARCHAR(255),
    banner_image        VARCHAR(255),
    address             VARCHAR(255),
    office_address      VARCHAR(255),
    CONSTRAINT pk_organization PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS organization_employee
(
    id            VARCHAR(255) NOT NULL,
    meedl_user_id VARCHAR(255),
    organization  VARCHAR(255),
    CONSTRAINT pk_organization_employee PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS organization_service_offering_entity
(
    id                         VARCHAR(255) NOT NULL,
    service_offering_entity_id VARCHAR(255),
    organization_id            VARCHAR(255),
    CONSTRAINT pk_organizationserviceofferingentity PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS program_cohort_entity
(
    id         VARCHAR(255) NOT NULL,
    cohort_id  VARCHAR(255),
    program_id VARCHAR(255),
    CONSTRAINT pk_programcohortentity PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS program_entity
(
    id                     VARCHAR(255) NOT NULL,
    program_description    VARCHAR(255),
    name                   VARCHAR(255),
    objectives             OID,
    duration_type          VARCHAR(255),
    duration               INTEGER      NOT NULL,
    number_of_trainees     INTEGER      NOT NULL,
    number_of_cohort       INTEGER      NOT NULL,
    mode                   VARCHAR(255),
    delivery_type          VARCHAR(255),
    program_status         VARCHAR(255),
    program_start_date     date,
    created_at             TIMESTAMP WITHOUT TIME ZONE,
    updated_at             TIMESTAMP WITHOUT TIME ZONE,
    created_by             VARCHAR(255) NOT NULL,
    updated_by             VARCHAR(255),
    organization_entity_id VARCHAR(255),
    CONSTRAINT pk_programentity PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS service_offering_entity
(
    id                      VARCHAR(255) NOT NULL,
    name                    VARCHAR(255),
    industry                VARCHAR(255),
    transaction_lower_bound DECIMAL,
    transaction_upper_bound DECIMAL,
    CONSTRAINT pk_serviceofferingentity PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS subject_entity
(
    id                   VARCHAR(255) NOT NULL,
    name                 VARCHAR(255),
    description          VARCHAR(255),
    curriculum_entity_id VARCHAR(255),
    CONSTRAINT pk_subject_entity PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS vendor_entity
(
    id                   VARCHAR(255) NOT NULL,
    product              SMALLINT,
    vendor_name          VARCHAR(255),
    terms_and_conditions VARCHAR(255),
    CONSTRAINT pk_vendorentity PRIMARY KEY (id)
);

ALTER TABLE cohort_entity
    ADD CONSTRAINT uc_cohortentity_name UNIQUE (name);

ALTER TABLE investment_vehicle_financier_entity_individuals
    ADD CONSTRAINT uc_investment_vehicle_financier_entity_individuals_individuals UNIQUE (individuals_id);

ALTER TABLE investment_vehicle_financier_entity_organizations
    ADD CONSTRAINT uc_investmentvehiclefinancierentityorganizations_organizations UNIQUE (organizations_id);

ALTER TABLE loan_product
    ADD CONSTRAINT uc_loan_product_name UNIQUE (name);

ALTER TABLE program_entity
    ADD CONSTRAINT uc_programentity_name UNIQUE (name);

ALTER TABLE vendor_entity
    ADD CONSTRAINT uc_vendorentity_vendorname UNIQUE (vendor_name);

ALTER TABLE organization_employee
    ADD CONSTRAINT uk_organization_employee UNIQUE (organization);

ALTER TABLE cohort_entity
    ADD CONSTRAINT FK_COHORTENTITY_ON_LOANDETAIL FOREIGN KEY (loan_detail_id) REFERENCES loan_detail_entity (id);

ALTER TABLE curriculum_entity
    ADD CONSTRAINT FK_CURRICULUM_ENTITY_ON_PROGRAMENTITY FOREIGN KEY (program_entity_id) REFERENCES program_entity (id);

ALTER TABLE investment_vehicle_entity
    ADD CONSTRAINT FK_INVESTMENTVEHICLEENTITY_ON_CONTRIBUTORS FOREIGN KEY (contributors_id) REFERENCES investment_vehicle_financier_entity (id);

ALTER TABLE investment_vehicle_entity
    ADD CONSTRAINT FK_INVESTMENTVEHICLEENTITY_ON_LEADS FOREIGN KEY (leads_id) REFERENCES investment_vehicle_financier_entity (id);

ALTER TABLE loan_breakdown_entity
    ADD CONSTRAINT FK_LOANBREAKDOWNENTITY_ON_COHORT FOREIGN KEY (cohort_id) REFERENCES cohort_entity (id);

ALTER TABLE loanee_entity
    ADD CONSTRAINT FK_LOANEEENTITY_ON_LOANEELOANDETAIL FOREIGN KEY (loanee_loan_detail_id) REFERENCES loanee_loan_detail_entity (id);

ALTER TABLE loanee_entity
    ADD CONSTRAINT FK_LOANEEENTITY_ON_USERIDENTITY FOREIGN KEY (user_identity_id) REFERENCES meedl_user (id);

ALTER TABLE loanee_loan_breakdown_entity
    ADD CONSTRAINT FK_LOANEELOANBREAKDOWNENTITY_ON_LOANEE FOREIGN KEY (loanee_id) REFERENCES loanee_entity (id);

ALTER TABLE loan_entity
    ADD CONSTRAINT FK_LOANENTITY_ON_LOANEEENTITY FOREIGN KEY (loanee_entity_id) REFERENCES loanee_entity (id);

ALTER TABLE loan_offer_entitiy
    ADD CONSTRAINT FK_LOANOFFERENTITIY_ON_LOANEE FOREIGN KEY (loanee_id) REFERENCES loanee_entity (id);

ALTER TABLE loan_offer_entitiy
    ADD CONSTRAINT FK_LOANOFFERENTITIY_ON_LOANPRODUCT FOREIGN KEY (loan_product_id) REFERENCES loan_product (id);

ALTER TABLE loan_offer_entitiy
    ADD CONSTRAINT FK_LOANOFFERENTITIY_ON_LOANREQUEST FOREIGN KEY (loan_request_id) REFERENCES loan_request_entity (id);

ALTER TABLE loan_product_vendor
    ADD CONSTRAINT FK_LOANPRODUCTVENDOR_ON_LOANPRODUCTENTITY FOREIGN KEY (loan_product_entity_id) REFERENCES loan_product (id);

ALTER TABLE loan_product_vendor
    ADD CONSTRAINT FK_LOANPRODUCTVENDOR_ON_VENDORENTITY FOREIGN KEY (vendor_entity_id) REFERENCES vendor_entity (id);

ALTER TABLE loan_referral_entity
    ADD CONSTRAINT FK_LOANREFERRALENTITY_ON_LOANEEENTITY FOREIGN KEY (loanee_entity_id) REFERENCES loanee_entity (id);

ALTER TABLE loan_request_entity
    ADD CONSTRAINT FK_LOANREQUESTENTITY_ON_LOANEEENTITY FOREIGN KEY (loanee_entity_id) REFERENCES loanee_entity (id);

ALTER TABLE next_of_kin_entity
    ADD CONSTRAINT FK_NEXTOFKINENTITY_ON_LOANEEENTITY FOREIGN KEY (loanee_entity_id) REFERENCES loanee_entity (id);

ALTER TABLE organization_service_offering_entity
    ADD CONSTRAINT FK_ORGANIZATIONSERVICEOFFERINGENTITY_ON_SERVICEOFFERINGENTITY FOREIGN KEY (service_offering_entity_id) REFERENCES service_offering_entity (id);

ALTER TABLE organization_employee
    ADD CONSTRAINT FK_ORGANIZATION_EMPLOYEE_ON_MEEDLUSER FOREIGN KEY (meedl_user_id) REFERENCES meedl_user (id);

ALTER TABLE program_cohort_entity
    ADD CONSTRAINT FK_PROGRAMCOHORTENTITY_ON_COHORT FOREIGN KEY (cohort_id) REFERENCES cohort_entity (id);

ALTER TABLE program_entity
    ADD CONSTRAINT FK_PROGRAMENTITY_ON_ORGANIZATIONENTITY FOREIGN KEY (organization_entity_id) REFERENCES organization (id);

ALTER TABLE subject_entity
    ADD CONSTRAINT FK_SUBJECT_ENTITY_ON_CURRICULUMENTITY FOREIGN KEY (curriculum_entity_id) REFERENCES curriculum_entity (id);

ALTER TABLE investment_vehicle_financier_entity_individuals
    ADD CONSTRAINT fk_invvehfinentind_on_investment_vehicle_financier_entity FOREIGN KEY (investment_vehicle_financier_entity_id) REFERENCES investment_vehicle_financier_entity (id);

ALTER TABLE investment_vehicle_financier_entity_individuals
    ADD CONSTRAINT fk_invvehfinentind_on_user_entity FOREIGN KEY (individuals_id) REFERENCES meedl_user (id);

ALTER TABLE investment_vehicle_financier_entity_organizations
    ADD CONSTRAINT fk_invvehfinentorg_on_investment_vehicle_financier_entity FOREIGN KEY (investment_vehicle_financier_entity_id) REFERENCES investment_vehicle_financier_entity (id);

ALTER TABLE investment_vehicle_financier_entity_organizations
    ADD CONSTRAINT fk_invvehfinentorg_on_organization_entity FOREIGN KEY (organizations_id) REFERENCES organization (id);

ALTER TABLE loan_product_entity_sponsors
    ADD CONSTRAINT fk_loanproductentity_sponsors_on_loan_product_entity FOREIGN KEY (loan_product_entity_id) REFERENCES loan_product (id);