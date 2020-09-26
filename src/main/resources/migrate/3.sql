-- Her kan i putte jeres næste migration.

USE chat;


CREATE TABLE logins (
log_id int NOT NULL AUTO_INCREMENT,
log_time varchar(45) NOT NULL,
fk_user_id int NOT NULL,
PRIMARY KEY (log_id),
FOREIGN KEY (fk_user_id) REFERENCES chat.users(user_id)
);


-- Husk at update jeres database version.
UPDATE properties
SET value = '3'
WHERE name = "version";
