-- Creating cohort_loan_detail_entity table
CREATE TABLE IF NOT EXISTS cohort_loan_detail_entity (
    id VARCHAR(36) PRIMARY KEY,
    cohort_id VARCHAR(36) NOT NULL,
    total_amount_requested DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    total_outstanding_amount DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    total_amount_received DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    FOREIGN KEY (cohort_id) REFERENCES cohort_entity(id),
);


find all existing cohort and create cohort_loan_detail_entity for the cohort


after creating the table find all CohortLoaneeEntity that have this cohort id after finding the CohortLoaneeEntity
                                    it has a relationship with loaneeLoandetail
                                    the loaneeLoanDetails the loaneeLoanedetais have and attribute amountRequested

                                    i want to calculate all the amount requested and sum it up in to the cohort loandetails amount requested