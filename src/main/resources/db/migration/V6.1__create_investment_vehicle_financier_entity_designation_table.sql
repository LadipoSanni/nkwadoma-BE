
-- Create the correct table if it doesn't exist
CREATE TABLE IF NOT EXISTS investment_vehicle_financier_entity_investment_vehicle_designat (
                                                                                              investment_vehicle_financier_entity_id VARCHAR(255) NOT NULL,
                                                                                              investment_vehicle_designation VARCHAR(255),
    CONSTRAINT fk_investment_vehicle_financier FOREIGN KEY (investment_vehicle_financier_entity_id)
    REFERENCES investment_vehicle_financier_entity (id)
    ON DELETE CASCADE
    );
