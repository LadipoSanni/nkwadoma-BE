-- Drop the incorrectly named column from the table
ALTER TABLE beneficial_owner_entity
DROP COLUMN IF EXISTS driver_licensetional_id_card;
