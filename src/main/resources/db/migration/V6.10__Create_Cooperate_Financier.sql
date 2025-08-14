CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS cooperate_financier_entity(
    id VARCHAR(36) PRIMARY KEY,
    cooperate_id VARCHAR(36),
    financier_id VARCHAR(36),
    activation_status VARCHAR(36),
    FOREIGN KEY (cooperate_id) REFERENCES cooperation_entity(id),
    FOREIGN KEY (financier_id) REFERENCES financier_entity(id)
    );