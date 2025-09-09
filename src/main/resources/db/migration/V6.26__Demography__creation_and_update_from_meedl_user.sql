-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create demography_entity table if it doesn't exist
CREATE TABLE IF NOT EXISTS demography_entity (
                                                 id VARCHAR(255) PRIMARY KEY,
                                                 name VARCHAR(255),
                                                 male_count INTEGER DEFAULT 0,
                                                 female_count INTEGER DEFAULT 0,
                                                 total_gender_count INTEGER DEFAULT 0,
                                                 age_17_to_25_count INTEGER DEFAULT 0,
                                                 age_25_to_35_count INTEGER DEFAULT 0,
                                                 age_35_to_45_count INTEGER DEFAULT 0,
                                                 south_east_count INTEGER DEFAULT 0,
                                                 south_west_count INTEGER DEFAULT 0,
                                                 south_south_count INTEGER DEFAULT 0,
                                                 north_east_count INTEGER DEFAULT 0,
                                                 north_west_count INTEGER DEFAULT 0,
                                                 north_central_count INTEGER DEFAULT 0,
                                                 non_nigerian INTEGER DEFAULT 0,
                                                 o_level_count INTEGER DEFAULT 0,
                                                 tertiary_count INTEGER DEFAULT 0
);

-- Create a custom function to try parsing multiple date formats
CREATE OR REPLACE FUNCTION TRY_TO_DATE(date_text TEXT) RETURNS DATE AS $$
BEGIN
    -- Try the expected format first
    RETURN TO_DATE(date_text, 'DDth Month YYYY');
EXCEPTION WHEN OTHERS THEN
    -- Try alternative formats
    BEGIN
        RETURN TO_DATE(date_text, 'DD-MM-YYYY');
    EXCEPTION WHEN OTHERS THEN
        BEGIN
            RETURN TO_DATE(date_text, 'MM-DD-YYYY');
        EXCEPTION WHEN OTHERS THEN
            RETURN NULL; -- Return NULL if no formats match
        END;
    END;
END;
$$ LANGUAGE plpgsql;

-- Insert data into demography_entity with flexible date parsing
INSERT INTO demography_entity (
    id, name,
    male_count, female_count, total_gender_count,
    age_17_to_25_count, age_25_to_35_count, age_35_to_45_count,
    south_east_count, south_west_count, south_south_count, north_east_count, north_west_count,
    north_central_count, non_nigerian,
    o_level_count, tertiary_count
)
SELECT
    uuid_generate_v4()::text AS id,
    'Meedl' AS name,
    -- Gender counts
    SUM(CASE WHEN gender = 'Male' THEN 1 ELSE 0 END) AS male_count,
    SUM(CASE WHEN gender = 'Female' THEN 1 ELSE 0 END) AS female_count,
    SUM(CASE WHEN gender IN ('Male', 'Female') THEN 1 ELSE 0 END) AS total_gender_count,
    -- Age groups with TRY_TO_DATE
    SUM(CASE WHEN date_of_birth IS NOT NULL
        AND TRY_TO_DATE(date_of_birth) IS NOT NULL
        AND date_part('year', age(current_date, TRY_TO_DATE(date_of_birth))) BETWEEN 17 AND 25
                 THEN 1 ELSE 0 END) AS age_17_to_25_count,
    SUM(CASE WHEN date_of_birth IS NOT NULL
        AND TRY_TO_DATE(date_of_birth) IS NOT NULL
        AND date_part('year', age(current_date, TRY_TO_DATE(date_of_birth))) BETWEEN 26 AND 35
                 THEN 1 ELSE 0 END) AS age_25_to_35_count,
    SUM(CASE WHEN date_of_birth IS NOT NULL
        AND TRY_TO_DATE(date_of_birth) IS NOT NULL
        AND date_part('year', age(current_date, TRY_TO_DATE(date_of_birth))) BETWEEN 36 AND 45
                 THEN 1 ELSE 0 END) AS age_35_to_45_count,
    -- Geopolitical zones
    SUM(CASE WHEN state_of_origin IS NOT NULL AND state_of_origin ILIKE ANY(ARRAY['Abia','Anambra','Ebonyi','Enugu','Imo']) THEN 1 ELSE 0 END) AS south_east_count,
    SUM(CASE WHEN state_of_origin IS NOT NULL AND state_of_origin ILIKE ANY(ARRAY['Ekiti','Lagos','Ogun','Ondo','Osun','Oyo']) THEN 1 ELSE 0 END) AS south_west_count,
    SUM(CASE WHEN state_of_origin IS NOT NULL AND state_of_origin ILIKE ANY(ARRAY['Akwa Ibom','Bayelsa','Cross River','Delta','Edo','Rivers']) THEN 1 ELSE 0 END) AS south_south_count,
    SUM(CASE WHEN state_of_origin IS NOT NULL AND state_of_origin ILIKE ANY(ARRAY['Adamawa','Bauchi','Borno','Gombe','Taraba','Yobe']) THEN 1 ELSE 0 END) AS north_east_count,
    SUM(CASE WHEN state_of_origin IS NOT NULL AND state_of_origin ILIKE ANY(ARRAY['Jigawa','Kaduna','Kano','Katsina','Kebbi','Sokoto','Zamfara']) THEN 1 ELSE 0 END) AS north_west_count,
    SUM(CASE WHEN state_of_origin IS NOT NULL AND state_of_origin ILIKE ANY(ARRAY['Benue','Kogi','Kwara','Nasarawa','Niger','Plateau','FCT']) THEN 1 ELSE 0 END) AS north_central_count,
    SUM(CASE WHEN state_of_origin IS NOT NULL AND state_of_origin NOT ILIKE ANY(
        ARRAY['Abia','Anambra','Ebonyi','Enugu','Imo',
            'Ekiti','Lagos','Ogun','Ondo','Osun','Oyo',
            'Akwa Ibom','Bayelsa','Cross River','Delta','Edo','Rivers',
            'Adamawa','Bauchi','Borno','Gombe','Taraba','Yobe',
            'Jigawa','Kaduna','Kano','Katsina','Kebbi','Sokoto','Zamfara',
            'Benue','Kogi','Kwara','Nasarawa','Niger','Plateau','FCT']
        ) THEN 1 ELSE 0 END) AS non_nigerian,
    0 AS o_level_count,
    0 AS tertiary_count
FROM meedl_user
WHERE role = 'LOANEE';