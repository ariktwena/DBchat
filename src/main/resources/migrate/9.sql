-- Her kan i putte jeres næste migration.

USE chat;

ALTER TABLE chat.users
ADD COLUMN salt BINARY(16) NOT NULL AFTER user_reg,
ADD COLUMN secret BINARY(32) NOT NULL AFTER salt;

-- Husk at update jeres database version.
UPDATE properties
SET value = '9'
WHERE name = "version";
