-- Her kan i putte jeres næste migration.

USE chat;

INSERT INTO rooms VALUES (2,'test_room2');

-- Husk at update jeres database version.
UPDATE properties
SET value = '6'
WHERE name = "version";
