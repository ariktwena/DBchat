-- Her kan i putte jeres næste migration.

USE chat;

CREATE TABLE rooms (
 room_id int NOT NULL AUTO_INCREMENT,
 room_name varchar(45) NOT NULL,
 PRIMARY KEY (room_id),
 UNIQUE KEY room_name_UNIQUE (room_name)
);

CREATE TABLE messages (
msg_id int unsigned NOT NULL AUTO_INCREMENT,
msg_content varchar(45) DEFAULT NULL,
msg_time varchar(45) NOT NULL,
fk_user_id int NOT NULL,
fk_room_id int NOT NULL,
PRIMARY KEY (msg_id),
FOREIGN KEY (fk_user_id) REFERENCES chat.users(user_id),
FOREIGN KEY (fk_room_id) REFERENCES chat.rooms(room_id)
);


CREATE TABLE subscriptions (
 sub_id int unsigned NOT NULL AUTO_INCREMENT,
 fk_user_id int DEFAULT NULL,
 fk_room_id int DEFAULT NULL,
 PRIMARY KEY (sub_id)
) ;


INSERT INTO users VALUES (12,'John','2020-09-24 11:28:52.654622'),(13,'Bob','2020-09-24 11:37:16.886425'),(14,'Sam','2020-09-25 11:09:55.478327');


-- Husk at update jeres database version.
UPDATE properties
SET value = '2'
WHERE name = "version";
