-- UMS Sample Data
-- ------------------------------------------------------

USE `ums`;

--
-- Insert data for table `role`
--

INSERT INTO role (`id`, `role_name`) VALUES ('1', 'Patient');
INSERT INTO role (`id`, `role_name`) VALUES ('2', 'Parents');
INSERT INTO role (`id`, `role_name`) VALUES ('3', 'Guardian');
INSERT INTO role (`id`, `role_name`) VALUES ('4', 'Provider');
INSERT INTO role (`id`, `role_name`) VALUES ('5', 'SystemSupport');
INSERT INTO role (`id`, `role_name`) VALUES ('6', 'StaffUser');

--
-- Insert data for table `scope`
--

INSERT INTO scope (`id`, `scope_name`,`scope_description`) VALUES ('1', 'c2sUiApi.read','Read access to C2S Backend API');
INSERT INTO scope (`id`, `scope_name`,`scope_description`) VALUES ('2', 'c2sUiApi.write','Write access to C2S Backend API');

--
-- Insert data for table `scope_roles`
--

INSERT INTO scope_roles (`scopes_id`, `roles_id`) VALUES ('1', '1');
INSERT INTO scope_roles (`scopes_id`, `roles_id`) VALUES ('2', '1');
