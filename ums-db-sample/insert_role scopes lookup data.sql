-- UMS Sample Data
-- ------------------------------------------------------

USE `ums`;

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

--
-- Insert data for table `scope`
--

INSERT INTO scope 
            (id, 
             scope_name, 
             scope_description) 
VALUES      ('1', 
             'c2sUiApi.read', 
             'Read access to C2S Backend API'), 
            ('2', 
             'c2sUiApi.write', 
             'Write access to C2S Backend API'); 

--
-- Insert data for table scope_roles
--

INSERT INTO role_scopes 
            (scopes_id, 
             roles_id) 
VALUES      ('1', 
             '1'), 
            ('2', 
             '1'); 