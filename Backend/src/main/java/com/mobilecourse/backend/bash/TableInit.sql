drop database Tilitili;
create database Tilitili;
use Tilitili;

create table User
(uid int primary key AUTO_INCREMENT,
username varchar(20) not null unique,
nickname varchar(20) default '',
email varchar(100) not null unique,
password varchar(100) not null,
department varchar(20) default '',
joinAt TIMESTAMP default CURRENT_TIMESTAMP,
avatar varchar(200) default '',
bio varchar(100) default '');

create table Plate
(pid int primary key AUTO_INCREMENT,
 type int default 0,
 title varchar(20) not null,
 description varchar(100) default '',
 cover varchar(200) default '');

create table PlateAuth
(pid int not null,
 uid int not null,
 primary key (pid, uid));

create table Submission
(sid int primary key AUTO_INCREMENT,
 uid int not null,
 type int not null,
 pid int not null,
 title varchar(100) default '',
 cover varchar(200) default '',
 introduction varchar(200) default '',
 resource varchar(200) default '',
 submissionTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 watchTimes int default 0);

create table Likes
(sid int not null,
 uid int not null,
 primary key (sid, uid));

create table Comment
(cid int primary key AUTO_INCREMENT,
 sid int not null,
 uid int not null,
 content varchar(200) default '',
 likes int default 0,
 commentTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP);

create table Follow
(followerUid int not null,
 followedUid int not null,
 primary key (followerUid, followedUid));

create table History
(uid int not null,
 sid int not null,
 watchTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 primary key (uid, sid));

insert into User(username, nickname, email, password, department, bio)
values ('Shion', 'ShionChan', 'shinoa_zwx@outlook.com', '12345678', 'Software', 'ShionChan Saikyo!');
insert into User(username, nickname, email, password, department, bio)
values ('Matsuri', 'MatsuriChan', 'shinoa_sama@outlook.com', '12345678', 'Software', 'Washoi!');

insert into Plate(title, description)
values ('Hololive', 'Idol Project');

insert into PlateAuth(pid, uid)
values (1, 1);
