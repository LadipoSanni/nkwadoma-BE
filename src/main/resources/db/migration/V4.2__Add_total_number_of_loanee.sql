DO $$
BEGIN
    IF NOT EXISTS (
        SELECT FROM information_schema.columns
        WHERE table_name = 'loan_product'
        AND column_name = 'total_number_of_loanee'
    ) THEN
ALTER TABLE loan_product
    ADD COLUMN total_number_of_loanee INTEGER;
END IF;
END $$;