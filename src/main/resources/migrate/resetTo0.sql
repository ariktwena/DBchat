DROP USER IF EXISTS 'testuser'@'localhost';
DROP DATABASE IF EXISTS chat;


CREATE DATABASE  IF NOT EXISTS `chat`;
CREATE USER  'testuser'@'localhost';
grant all privileges on chat.* TO 'testuser'@'localhost';

USE `chat`;

DROP TABLE IF EXISTS properties;
CREATE TABLE properties (
name VARCHAR(255) PRIMARY KEY,
value VARCHAR(255) NOT NULL
);

INSERT INTO properties (name, value) VALUES ("version", "0");