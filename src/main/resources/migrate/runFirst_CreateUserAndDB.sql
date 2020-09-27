DROP USER IF EXISTS 'testuser'@'localhost';
DROP DATABASE IF EXISTS chat;


CREATE DATABASE IF NOT EXISTS chat;
CREATE USER 'testuser'@'localhost';
grant all privileges on chat.* TO 'testuser'@'localhost';

