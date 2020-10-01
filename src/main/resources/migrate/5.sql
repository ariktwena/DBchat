-- Her kan i putte jeres næste migration.

USE chat;



-- Husk at update jeres database version.
UPDATE properties
SET value = '5'
WHERE name = "version";
