-- Her kan i putte jeres næste migration.

USE chat;

INSERT INTO logins VALUES (1,'2020-09-26 23:17:17.246785',14);

INSERT INTO online VALUES (1,'2020-09-26 23:17:17.298255',14);

INSERT INTO rooms VALUES (1,'test_room1');

-- Husk at update jeres database version.
UPDATE properties
SET value = '5'
WHERE name = "version";
