
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


-- Create table for EntityBankDetail
CREATE TABLE entity_bank_detail (
                                    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                    entity_id VARCHAR(255) NOT NULL,
                                    bank_detail_id VARCHAR(255) UNIQUE, -- one-to-one relationship

                                    CONSTRAINT fk_entity_bank_detail_bank_detail
                                        FOREIGN KEY (bank_detail_id)
                                            REFERENCES bank_detail_entity(id)
);
