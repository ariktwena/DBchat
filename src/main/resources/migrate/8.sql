-- Her kan i putte jeres næste migration.

USE chat;

CREATE TABLE private_messages (
p_msg_id int unsigned NOT NULL AUTO_INCREMENT,
p_msg_content varchar(45) DEFAULT NULL,
p_msg_time varchar(45) NOT NULL,
fk_from_user_id int NOT NULL,
fk_to_user_id int NOT NULL,
fk_room_id int NOT NULL,
PRIMARY KEY (p_msg_id),
FOREIGN KEY (fk_from_user_id) REFERENCES chat.users(user_id),
FOREIGN KEY (fk_to_user_id) REFERENCES chat.users(user_id),
FOREIGN KEY (fk_room_id) REFERENCES chat.rooms(room_id)
);

-- Husk at update jeres database version.
UPDATE properties
SET value = '8'
WHERE name = "version";
