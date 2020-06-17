drop database Tilitili;
create database Tilitili;
use Tilitili;

create table User(id int primary key AUTO_INCREMENT,
username varchar(20) not null unique,
nickname varchar(20),
email varchar(100) not null unique,
password varchar(100) not null,
privilege int DEFAULT 0,
organization varchar(20),
department varchar(20),
joinAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
avatar int);