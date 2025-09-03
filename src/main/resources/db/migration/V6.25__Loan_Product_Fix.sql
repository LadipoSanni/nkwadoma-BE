ALTER TABLE loan_product
    ALTER COLUMN loan_product_sponsors TYPE TEXT[] USING (ARRAY[loan_product_sponsors]::TEXT[]);
ALTER TABLE loan_product
    ALTER COLUMN loan_product_sponsors SET DEFAULT '{}';