-- Recreate the FinancierEntity table
CREATE TABLE financier_entity (
                                  id VARCHAR(255) PRIMARY KEY,
                                  organization_name VARCHAR(255),
                                  individual_id VARCHAR(255),
                                  investment_vehicle_role VARCHAR(255),
                                  invited_by VARCHAR(255),
                                  CONSTRAINT financier_entity_individual_fk FOREIGN KEY (individual_id) REFERENCES meedl_user(id)
);
-- Drop existing table if it exists
DROP TABLE IF EXISTS investment_vehicle_financier_entity CASCADE;

-- Recreate the InvestmentVehicleFinancierEntity table
CREATE TABLE investment_vehicle_financier_entity (
                                                     id VARCHAR(255) PRIMARY KEY,
                                                     financier_id VARCHAR(255) NOT NULL,
                                                     investment_vehicle_id VARCHAR(255) NOT NULL,
                                                     CONSTRAINT investment_vehicle_financier_entity_financier_fk FOREIGN KEY (financier_id) REFERENCES meedl_user(id),
                                                     CONSTRAINT investment_vehicle_financier_entity_investment_vehicle_fk FOREIGN KEY (investment_vehicle_id) REFERENCES investment_vehicle_entity(id)
);

-- Drop the existing tables if they exist
DROP TABLE IF EXISTS investment_vehicle_financier_entity_individuals CASCADE;
DROP TABLE IF EXISTS investment_vehicle_financier_entity_organizations CASCADE;

-- Create BankAccountEntity table
CREATE TABLE bank_account_entity (
                                     id VARCHAR(255) PRIMARY KEY
);

-- Create CapitalDistributionEntity table
CREATE TABLE capital_distribution_entity (
                                             id VARCHAR(255) PRIMARY KEY,
                                             due INT NOT NULL,
                                             total_capital_paid_out NUMERIC(19,2) NOT NULL
);

-- Create CouponDistributionEntity table
CREATE TABLE coupon_distribution_entity (
                                            id VARCHAR(255) PRIMARY KEY,
                                            due INT NOT NULL,
                                            paid INT NOT NULL,
                                            last_date_paid TIMESTAMP,
                                            last_date_due TIMESTAMP
);

-- Create VehicleClosureEntity table
CREATE TABLE vehicle_closure_entity (
                                        id VARCHAR(255) PRIMARY KEY,
                                        investment_vehicle_mode VARCHAR(255) NOT NULL,
                                        capital_distribution_id VARCHAR(255),
                                        maturity VARCHAR(255),
                                        CONSTRAINT vehicle_closure_capital_distribution_fk FOREIGN KEY (capital_distribution_id) REFERENCES capital_distribution_entity(id)
);

-- Create VehicleOperationEntity table
CREATE TABLE vehicle_operation_entity (
                                          id VARCHAR(255) PRIMARY KEY,
                                          coupon_distribution_status VARCHAR(255) NOT NULL,
                                          coupon_distribution_id VARCHAR(255),
                                          fund_raising_status VARCHAR(255) NOT NULL,
                                          deploying_status VARCHAR(255) NOT NULL,
                                          CONSTRAINT vehicle_operation_coupon_distribution_fk FOREIGN KEY (coupon_distribution_id) REFERENCES coupon_distribution_entity(id)
);


-- Alter InvestmentVehicleEntity to add new fields
ALTER TABLE investment_vehicle_entity
    ADD COLUMN operation_id VARCHAR(255),
ADD COLUMN closure_id VARCHAR(255),
ADD COLUMN main_account_id VARCHAR(255),
ADD COLUMN syncing_account_id VARCHAR(255),
ADD CONSTRAINT investment_vehicle_operation_fk FOREIGN KEY (operation_id) REFERENCES vehicle_operation_entity(id),
ADD CONSTRAINT investment_vehicle_closure_fk FOREIGN KEY (closure_id) REFERENCES vehicle_closure_entity(id),
ADD CONSTRAINT investment_vehicle_main_account_fk FOREIGN KEY (main_account_id) REFERENCES bank_account_entity(id),
ADD CONSTRAINT investment_vehicle_syncing_account_fk FOREIGN KEY (syncing_account_id) REFERENCES bank_account_entity(id);
