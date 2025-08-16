BEGIN;

ALTER TABLE cooperation_entity
    ADD COLUMN IF NOT EXISTS email VARCHAR(36);


CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

INSERT INTO cooperate_financier_entity (
     id,
     cooperate,
     financier,
     activation_status
)
SELECT
    uuid_generate_v4(),
    ce.id ,
    fe.id,
    fe.activation_status

FROM cooperation_entity ce
     JOIN financier_entity fe on fe.cooperation_id = ce.id
     JOIN meedl_user mu on mu.id = fe.user_identity_id
WHERE
    fe.cooperation_id IS NOT NULL;


ALTER TABLE financier_entity
    DROP COLUMN IF EXISTS cooperation_id


ALTER TABLE cooperate_financier_entity
    ADD CONSTRAINT fk_cooperate FOREIGN KEY (cooperate) REFERENCES cooperation_entity(id),
    ADD CONSTRAINT fk_financier FOREIGN KEY (financier) REFERENCES financier_entity(id);


 COMMIT;