-- ------------------------------------------------------ 
-- UMS Sample Data 
-- ------------------------------------------------------ 
USE ums; 

-- 
-- Insert data for table state_code 
-- 
INSERT INTO state_code 
            (id, 
             code, 
             code_system, 
             code_system_name, 
             code_systemoid, 
             description, 
             display_name) 
VALUES      ('1', 
             'AW', 
             'uid:orn:2.16.840.1.113883.3.88.12.80.63', 
             'Country', 
             'uid:orn:2.16.840.1.113883.3.88.12.80.63', 
             'Aruba', 
             'Aruba'), 
            ('2', 
             'MD', 
             'uid:orn:2.16.840.1.113883.3.88.12.80.63', 
             'Country', 
             'uid:orn:2.16.840.1.113883.3.88.12.80.63', 
             'MARYLAND', 
             'MARYLAND'); 