
-- 1. Modify existing VendorEntity table
ALTER TABLE vendor_entity
    ADD COLUMN cost_of_service NUMERIC(19, 2),
    ADD COLUMN duration INT;

-- 2. Create DisbursementRuleEntity table
CREATE TABLE IF NOT EXISTS disbursement_rule_entity (
                                          id VARCHAR(36) PRIMARY KEY,
                                          name VARCHAR(255) NOT NULL,
                                          query TEXT NOT NULL,
                                          activation_status VARCHAR(50) NOT NULL
);

-- 3. Create LoanProductRule (LoanProductDisbursementRuleEntity)
CREATE TABLE IF NOT EXISTS loan_product_disb_rule (
                                   id VARCHAR(36) PRIMARY KEY,
                                    loan_product_entity_id VARCHAR(36) NOT NULL,
                                   disbursement_rule_entity_id VARCHAR(36) NOT NULL,

                                   CONSTRAINT fk_loan_product FOREIGN KEY (loan_product_entity_id)
                                       REFERENCES loan_product(id),
                                   CONSTRAINT fk_disbursement_rule FOREIGN KEY (disbursement_rule_entity_id)
                                       REFERENCES disbursement_rule_entity(id)
);
