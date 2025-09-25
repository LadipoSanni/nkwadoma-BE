

-- V6.14__add_portfolio_and_platform_request.sql

-- 1. Add new column to PortfolioEntity
ALTER TABLE portfolio_entity
    ADD COLUMN IF NOT EXISTS obligor_loan_limit NUMERIC(19,2) DEFAULT 0 ;

-- 2. Create new table for PlatformRequestEntity
CREATE TABLE IF NOT EXISTS platform_request_entity (
    id VARCHAR(36) PRIMARY KEY,
    obligor_loan_limit NUMERIC(19,2),
    created_by VARCHAR(255),
    request_time TIMESTAMP
    );
