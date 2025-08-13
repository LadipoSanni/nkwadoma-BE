ALTER TABLE cohort_entity
    ADD COLUMN IF NOT EXISTS cohort_type VARCHAR(50) NOT NULL DEFAULT 'NON_LOAN_BOOK';


-- Updating cohort_type based on the role of the user in created_by
UPDATE cohort_entity cohort
SET cohort_type = CASE
                      WHEN EXISTS (
                          SELECT 1
                          FROM meedl_user u
                          WHERE u.id = cohort.created_by
                            AND u.role IN ('MEEDL_ASSOCIATE', 'MEEDL_SUPER_ADMIN', 'MEEDL_ADMIN', 'PORTFOLIO_MANAGER')
                      ) THEN 'LOAN_BOOK'
                      ELSE 'NON_LOAN_BOOK'
END;