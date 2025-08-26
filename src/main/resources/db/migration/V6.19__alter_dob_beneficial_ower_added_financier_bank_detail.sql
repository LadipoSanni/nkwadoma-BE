-- Change DOB column from TIMESTAMP to DATE
ALTER TABLE beneficial_owner_entity
ALTER COLUMN beneficial_owner_date_of_birth TYPE DATE
    USING beneficial_owner_date_of_birth::DATE;
