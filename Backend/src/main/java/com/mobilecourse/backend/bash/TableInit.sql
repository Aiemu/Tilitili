drop database Tilitili;
create database Tilitili;
use Tilitili;

create table User
(id int primary key AUTO_INCREMENT,
username varchar(20) not null unique,
nickname varchar(20) default '',
email varchar(100) not null unique,
password varchar(100) not null,
department varchar(20) default '',
joinAt TIMESTAMP default CURRENT_TIMESTAMP,
avatar varchar(200) default '',
bio varchar(100) default '');

create table Plate
(id int primary key AUTO_INCREMENT,
 title varchar(20) not null,
 description varchar(100) default '');

create table PlateAuth
(pid int not null,
 uid int not null,
 auth int not null);

# create table Department
# (id int primary key AUTO_INCREMENT,
#  name varchar(20) not null unique,
#  pid int);

create table Submission
(id int primary key AUTO_INCREMENT,
 type int not null,
 pid int,
 title varchar(100),
 cover varchar(200),
 introduction varchar(200),
 resource varchar(200) not null,
 submissionTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 likes int default 0,
 watchTimes int default 0);


create table Comment
(sid int not null,
 uid int not null,
 content varchar(200),
 likes int default 0,
 commentTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP);