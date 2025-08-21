CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


CREATE TABLE institute_metrics_entity (
       id VARCHAR(36) PRIMARY KEY,
       number_of_programs INTEGER NOT NULL DEFAULT 0,
       number_of_loanees INTEGER NOT NULL DEFAULT 0,
       still_in_training INTEGER NOT NULL DEFAULT 0,
       number_of_cohort INTEGER NOT NULL DEFAULT 0,
       organization_id VARCHAR(255) NOT NULL,
       CONSTRAINT fk_organization
           FOREIGN KEY (organization_id)
               REFERENCES organization(id)
               ON DELETE CASCADE
);


INSERT INTO institute_metrics_entity(
         id,
         number_of_programs,
         number_of_loanees,
         still_in_training,
         number_of_cohort,
         organization_id
)

SELECT
    uuid_generate_v4(),
     o.number_of_programs  as number_of_programs ,
     o.number_of_loanees as number_of_loanees,
     o.still_in_training as still_in_training,
     o.number_of_cohort as number_of_cohort ,
     o.id as organization_id
FROM organization o
GROUP BY o.id;

ALTER TABLE organization
    DROP COLUMN IF EXISTS number_of_programs,
    DROP COLUMN IF EXISTS number_of_loanees,
    DROP COLUMN IF EXISTS still_in_training,
    DROP COLUMN IF EXISTS number_of_cohort;
