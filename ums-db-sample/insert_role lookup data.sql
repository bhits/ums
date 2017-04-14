-- ------------------------------------------------------ 
-- UMS Sample Data 
-- ------------------------------------------------------ 
USE ums; 

-- 
-- Insert data for table role 
-- 
INSERT INTO role 
            (id, 
             role_name) 
VALUES      ('1', 
             'Patient'), 
            ('2', 
             'Parents'), 
            ('3', 
             'Guardian'), 
            ('4', 
             'Provider'), 
            ('5', 
             'SystemSupport'), 
            ('6', 
             'StaffUser'); 