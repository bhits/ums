-- ------------------------------------------------------ 
-- UMS Sample Data 
-- ------------------------------------------------------ 
USE ums; 

-- 
-- Insert data for table country_code 
-- 
INSERT INTO country_code 
            (id, 
             code, 
             code_system, 
             code_system_name, 
             code_systemoid, 
             description, 
             display_name) 
VALUES      ('1', 
             'US', 
             '1.0.3166.1', 
             'ISO 3166-1 Country Codes', 
             'urn:oid:1.0.3166.1', 
             'United States', 
             'United States'), 
            ('2', 
             'KZ', 
             '1.0.3166.1', 
             'ISO 3166-1 Country Codes', 
             'urn:oid:1.0.3166.1', 
             'Kazakhstan', 
             'Kazakhstan'), 
            ('3', 
             'IL', 
             '1.0.3166.1', 
             'ISO 3166-1 Country Codes', 
             'urn:oid:1.0.3166.1', 
             'Israel', 
             'Israel'), 
            ('4', 
             'AW', 
             '1.0.3166.1', 
             'ISO 3166-1 Country Codes', 
             'urn:oid:1.0.3166.1', 
             'Aruba', 
             'Aruba'); 