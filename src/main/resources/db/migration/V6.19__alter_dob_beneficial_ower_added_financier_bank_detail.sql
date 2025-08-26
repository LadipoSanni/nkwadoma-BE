-- Change DOB column from TIMESTAMP to DATE
ALTER TABLE beneficial_owner_entity
ALTER COLUMN beneficial_owner_date_of_birth TYPE DATE
    USING beneficial_owner_date_of_birth::DATE;


CREATE TABLE financier_bank_detail_entity (
                                              id VARCHAR(255) PRIMARY KEY NOT NULL,
                                              financier_entity_id VARCHAR(255) REFERENCES financier_entity(id),
                                              bank_detail_entity_id VARCHAR(255) REFERENCES bank_detail_entity(id)
);

-- Change DOB column from TIMESTAMP to DATE
ALTER TABLE beneficial_owner_entity
ALTER COLUMN beneficial_owner_date_of_birth TYPE DATE
    USING beneficial_owner_date_of_birth::DATE;

DROP TABLE IF EXISTS entity_bank_detail CASCADE;