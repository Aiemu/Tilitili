drop database Tilitili;
create database Tilitili;
use Tilitili;

set character_set_database=utf8;
set character_set_server=utf8;
set character_set_client=utf8;
set character_set_connection=utf8;

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

create table Favorite
(sid int not null,
 uid int not null,
 favoriteTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
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

create table Message
(mid int primary key AUTO_INCREMENT,
 srcUid int not null,
 destUid int not null,
 type int default 0,
 content varchar(200) default '',
 messageTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP);


# 密码都为12345678
insert into User(uid, username, nickname, email, password, department, bio)
values (1, 'Matsuri', 'MatsuriChan', 'shinoa_sama@outlook.com', 'cdtt1sj/6IMMba0K881Jmg==', 'Software', 'Washoi!');
insert into User(uid, username, nickname, email, password, department, bio, avatar)
values (2, 'Tsinghua', 'THU Official', 'tsinghua@tsinghua.edu.cn', 'cdtt1sj/6IMMba0K881Jmg==', 'Campus', 'No.1 University', '/image/campus.png');


insert into Plate(pid, title, description, cover)
values (1, '学习', '学习心得交流、资料分享', '/image/study.png');
insert into Plate(pid, title, description, cover)
values (2, '生活', '今天三教开了吗', '/image/life.png');
insert into Plate(pid, title, description, cover)
values (3, '运动', '老马杯项目冠军了', '/image/sport.png');
insert into Plate(pid, title, description, cover)
values (4, '树洞', '嘤嘤嘤', '/image/treehole.png');
insert into Plate(pid, title, description, cover)
values (5, '其他', '杂谈, 委托根据地', '/image/other.png');
insert into Plate(pid, title, description, cover)
values (6, '娱乐', '我五杀又被人抢了', '/image/entertainment.png');

insert into Plate(pid, type, title, description, cover)
values (7, 1, '校园新闻', '官方新闻发布', '/image/campus.png');
insert into Plate(pid, type, title, description, cover)
values (8, 1, '院系宣传', '院系风采展示', '/image/department.png');

insert into PlateAuth(pid, uid)
values (7, 2);
insert into PlateAuth(pid, uid)
values (8, 2);

INSERT INTO Submission(uid, type, pid, title, cover, introduction, resource)
values (1, 1, 5, 'Matsuri 2nd anniversary', '', '夏色祭出道2周年live节选', '/video/demo.mp4');
