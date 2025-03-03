-- Recreate the FinancierEntity table
CREATE TABLE financier_entity (
                                  id VARCHAR(255) PRIMARY KEY,
                                  investment_vehicle_role VARCHAR(255),
                                  created_by VARCHAR(255)
);


CREATE TABLE investment_vehicle_financier_entity_join (
                                                     id VARCHAR(255) PRIMARY KEY,
                                                     financier_id VARCHAR(255) NOT NULL,
                                                     investment_vehicle_id VARCHAR(255) NOT NULL,
                                                     CONSTRAINT investment_vehicle_financier_entity_financier_fk FOREIGN KEY (financier_id) REFERENCES meedl_user(id),
                                                     CONSTRAINT investment_vehicle_financier_entity_investment_vehicle_fk FOREIGN KEY (investment_vehicle_id) REFERENCES investment_vehicle_entity(id)
);
