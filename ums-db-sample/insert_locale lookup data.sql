-- UMS Sample Data
-- ------------------------------------------------------

USE ums;

--
-- Insert data for table locale
--
INSERT INTO locale 
            (id, 
             code, 
             code_system, 
             code_system_name, 
             code_systemoid, 
             description, 
             display_name) 
VALUES      ('1', 
             'en', 
             'urn:oid:2.16.840.1.113883.6.121', 
             'Language', 
             'urn:oid:2.16.840.1.113883.6.121', 
             'English', 
             'English'), 
            ('2', 
             'es', 
             'urn:oid:2.16.840.1.113883.6.121', 
             'Language', 
             'urn:oid:2.16.840.1.113883.6.121', 
             'Spanish', 
             'Spanish'); 