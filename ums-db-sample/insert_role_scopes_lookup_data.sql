-- UMS Sample Data
-- ------------------------------------------------------

USE ums;

--
-- Insert data for table role
--

INSERT INTO role
(id,
 code,
 name)
VALUES
  ('1',
   'patient',
   'Patient'),
  ('2',
   'parents',
   'Parents'),
  ('3',
   'guardian',
   'Guardian'),
  ('4',
   'provider',
   'Provider'),
  ('5',
   'systemSupport',
   'System Support'),
  ('6',
   'staff',
   'Staff');

--
-- Insert data for table relationship
--

INSERT INTO relationship
(role_id)
VALUES
  ('1'),
  ('2'),
  ('3');

--
-- Insert data for table scope
--

INSERT INTO scope
(id,
 scope_name,
 scope_description)
VALUES
  ('1',
   'c2sUiApi.read',
   'Read access to C2S Backend API'),
  ('2',
   'c2sUiApi.write',
   'Write access to C2S Backend API'),
  ('3',
   'c2sUi.access',
   'Access C2S UI'),
  ('4',
   'providerUiApi.read',
   'Read Provider UI backend resource'),
  ('5',
   'providerUiApi.write',
   'Write Provider UI backend resource'),
  ('6',
   'providerUi.access',
   'Access Provider UI');

--
-- Insert data for table scope_roles
--

INSERT INTO role_scopes
(scopes_id,
 roles_id)
VALUES
  ('1',
   '1'),
  ('2',
   '1'),
  ('3',
   '1'),
  ('4',
   '4'),
  ('5',
   '4'),
  ('6',
   '4');