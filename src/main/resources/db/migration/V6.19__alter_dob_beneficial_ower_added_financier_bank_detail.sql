-- Change DOB column from TIMESTAMP to DATE
ALTER TABLE beneficial_owner_entity
ALTER COLUMN beneficial_owner_date_of_birth TYPE DATE
    USING beneficial_owner_date_of_birth::DATE;


CREATE TABLE IF NOT EXISTS financier_bank_detail_entity (
        id VARCHAR(255) PRIMARY KEY NOT NULL,
        financier_entity_id VARCHAR(255) REFERENCES financier_entity(id),
        bank_detail_entity_id VARCHAR(255) REFERENCES bank_detail_entity(id)
);

CREATE TABLE organization_bank_detail_entity (
      id VARCHAR(255) PRIMARY KEY NOT NULL,
      organization_id VARCHAR(255) REFERENCES organization(id),
      bank_detail_entity_id VARCHAR(255) REFERENCES bank_detail_entity(id)
);
CREATE TABLE loanee_bank_detail_entity (
    id VARCHAR(255) PRIMARY KEY  NOT NULL,
    loanee_entity_id VARCHAR(255) REFERENCES loanee_entity(id),
    bank_detail_entity_id VARCHAR(255) REFERENCES bank_detail_entity(id)
);
-- Change DOB column from TIMESTAMP to DATE
ALTER TABLE beneficial_owner_entity
ALTER COLUMN beneficial_owner_date_of_birth TYPE DATE
    USING beneficial_owner_date_of_birth::DATE;

DROP TABLE IF EXISTS entity_bank_detail CASCADE;