-- Her kan i putte jeres næste migration.

USE chat;

CREATE TABLE users (
user_id int NOT NULL AUTO_INCREMENT,
user_name varchar(45) NOT NULL,
user_reg varchar(45) DEFAULT NULL,
PRIMARY KEY (user_id),
UNIQUE KEY user_name_UNIQUE (user_name),
KEY user_id (user_id)
);


-- Husk at update jeres database version.
UPDATE properties
SET value = '1'
WHERE name = "version";
