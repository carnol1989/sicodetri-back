--src/test/resources/db/data-security.sql
-- 1) Crea los roles de negocio
INSERT INTO roles (name, enabled) VALUES
  ('AssetUploader',       true),
  ('TaxTableViewer',      true),
  ('DepreciationProcessor', true),
  ('ReportGenerator',     true);

-- 2) Inserta usuarios de prueba con contrase?a "Password123!" (hasheada con BCrypt)
--    Hash obtenido con BCryptPasswordEncoder: $2b$12$Fo2HJujzYZkjb4LbnQef1eM.y.95wNyiqqs841s58MF0d/qWb8qQy
INSERT INTO users (username, password, enabled, added_by, addition_date) VALUES
  ('asset_uploader',     '$2b$12$Fo2HJujzYZkjb4LbnQef1eM.y.95wNyiqqs841s58MF0d/qWb8qQy', true, 'system', now()),
  ('tax_viewer',         '$2b$12$Fo2HJujzYZkjb4LbnQef1eM.y.95wNyiqqs841s58MF0d/qWb8qQy', true, 'system', now()),
  ('depreciation_proc',  '$2b$12$Fo2HJujzYZkjb4LbnQef1eM.y.95wNyiqqs841s58MF0d/qWb8qQy', true, 'system', now()),
  ('report_generator',   '$2b$12$Fo2HJujzYZkjb4LbnQef1eM.y.95wNyiqqs841s58MF0d/qWb8qQy', true, 'system', now()),
  ('katherine_gutierrez',   '$2b$12$Fo2HJujzYZkjb4LbnQef1eM.y.95wNyiqqs841s58MF0d/qWb8qQy', true, 'system', now());

-- 3) Asocia cada usuario con su(s) rol(es)
 INSERT INTO user_roles (user_id, role_id) VALUES
 (1,1),
 (2,2),
 (3,3),
 (4,4),
 (5,1),
 (5,2),
 (5,3),
 (5,4);