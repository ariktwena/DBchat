-- Her kan i putte jeres næste migration.

USE chat;


CREATE TABLE online (
online_id int NOT NULL AUTO_INCREMENT,
online_time varchar(45) NOT NULL,
fk_user_id int NOT NULL,
PRIMARY KEY (online_id),
FOREIGN KEY (fk_user_id) REFERENCES chat.users(user_id)
);


-- Husk at update jeres database version.
UPDATE properties
SET value = '4'
WHERE name = "version";
