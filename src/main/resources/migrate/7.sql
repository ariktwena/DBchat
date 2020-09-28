-- Her kan i putte jeres næste migration.

USE chat;

ALTER TABLE subscriptions
ADD COLUMN sub_time VARCHAR(45) NOT NULL AFTER sub_id,
CHANGE COLUMN fk_user_id fk_user_id INT NOT NULL ,
CHANGE COLUMN fk_room_id fk_room_id INT NOT NULL ;

-- Husk at update jeres database version.
UPDATE properties
SET value = '7'
WHERE name = "version";
