INSERT INTO administrative_gender_code 
            (id, 
             code, 
             code_system, 
             code_system_name, 
             code_systemoid, 
             display_name, 
             original_text) 
VALUES      ('1', 
             'male', 
             'http://hl7.org/fhir/administrative-gender', 
             'AdministrativeGender', 
             '2.16.840.1.113883.4.642.1.2', 
             'Male', 
             'Male'); 

INSERT INTO ums.administrative_gender_code 
            (id, 
             code, 
             code_system, 
             code_system_name, 
             code_systemoid, 
             display_name, 
             original_text) 
VALUES      ('2', 
             'female', 
             'http://hl7.org/fhir/administrative-gender', 
             'AdministrativeGender', 
             '2.16.840.1.113883.4.642.1.2', 
             'Female', 
             'Female'); 

INSERT INTO ums.administrative_gender_code 
            (id, 
             code, 
             code_system, 
             code_system_name, 
             code_systemoid, 
             display_name, 
             original_text) 
VALUES      ('3', 
             'other', 
             'http://hl7.org/fhir/administrative-gender', 
             'AdministrativeGender', 
             '2.16.840.1.113883.4.642.1.2', 
             'Other', 
             'Other'); 

INSERT INTO ums.administrative_gender_code 
            (id, 
             code, 
             code_system, 
             code_system_name, 
             code_systemoid, 
             display_name, 
             original_text) 
VALUES      ('4', 
             'unknown', 
             'http://hl7.org/fhir/administrative-gender', 
             'AdministrativeGender', 
             '2.16.840.1.113883.4.642.1.2', 
             'Unknown', 
             'Unknown'); 