Create Table if not exists cohort_loanee (
    id VARCHAR(36) PRIMARY KEY,
    cohort_id VARCHAR(36) NOT NULL,
    loanee_id VARCHAR(36) NOT NULL,
    created_by VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    loanee_loan_detail_id VARCHAR(36),
    loanee_status VARCHAR(50) NOT NULL,
    onboarding_mode VARCHAR(50) NOT NULL,
    uploaded_status VARCHAR(50) NOT NULL,
    referral_date_time TIMESTAMP,
    referred_by VARCHAR(255),
    reason_for_dropout TEXT,
    deferred_date_and_time TIMESTAMP,
    defer_reason TEXT,
    deferral_requested BOOLEAN NOT NULL DEFAULT FALSE,
    deferral_approved BOOLEAN NOT NULL DEFAULT FALSE,
    dropout_requested BOOLEAN NOT NULL DEFAULT FALSE,
    dropout_approved BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (cohort_id) REFERENCES cohort(id),
    FOREIGN KEY (loanee_id) REFERENCES loanee(id),
    FOREIGN KEY (loanee_loan_detail_id) REFERENCES loanee_loan_detail(id)
);


