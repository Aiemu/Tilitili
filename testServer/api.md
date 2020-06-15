# 后端技术文档

## 变量语义对照表

| zh         | en          | 说明                       | 
| ---------- | ----------- | -------------------------- |
| 用户        | User        | 帐号                       | 
| 用户ID      | ID          | 帐号惟一标识符，字母数字   |
| 昵称       | Nickname    | 可以随意更改的显示名       |
| 签名       | Bio         | 个性签名         |
| 站长权限    | Superuser   | 全站最高管理权限           |
| 版主       | Moderator   | 管理板块管理员、拥有对该版块的所有权限 |
| 版块管理员  | Organizer   |  拥有对该版块的所有权限 |
| 登录       | Log in      | —                          |
| 注册       | Sign up     | —                          |
| 个人主页   | Profile     | —                          |
| 投稿       | Submission     | —                          |

## 数据库设计

### 用户数据

Scheme: User
| Attribute属性名 | Meaning意义 | Type数据类型 | Constraint约束 |
| -------------- | ---------- | ----------- | ------------- |
| id             | 用户全局唯一id| int        | primary key   |
| username		 | 登录名(可以考虑学号)| varchar(20)| not null 	 |
| nickname       | 用户昵称     | varchar(20) |              |
| email          | 清华邮箱 | varchar(30) |               |
| password       | 密码        | varchar(20) | not null     |
| previlege      | 权限        | int         |               |
| joinAt         | 注册时间    | date        |               |
| avatar         | 头像        | int        |                |

### 板块

Schema: Plate
| Attribute属性名 | Meaning意义 | Type数据类型 | Constraint约束 |
| -------------- | ---------- | ----------- | ------------- |
| id             | 板块全局唯一id| int        | primary key   |
| title          | 板块名称     | varchar(20) | not null   |
| owner          | 版主        | int        | reference User.id|
| startTime      | 创办时间     | date        | not null     |
| description    | 简介        | varchar(200)|              |


### 投稿内容

Schema: Submission
| Attribute属性名 | Meaning意义 | Type数据类型 | Constraint约束 |
| -------------- | ---------- | ----------- | ------------- |
| id             | 投稿内容唯一id | int       | primary key   |
| pid			 | 投稿板块		| int		  | reference Plate.id|
| type           | 投稿类型    | int         |               |
| resource       | 内容资源(视频, 文章) | URI |                |
| watchTimes     | 观看次数     | int        | not negative  |

### 板块参与者

Schema: PlateParticipation
| Attribute属性名 | Meaning意义 | Type数据类型 | Constraint约束 |
| -------------- | ---------- | ----------- | ------------- |
| uid            | 用户id      | int        |reference User.id|
| pid            | 板块id      | int        | reference Plate.id|
| type           | 参与者类型   | int        |                |

## 部分数据结构设计

### 用户权限设计
- **UserPrivilegeNormal** 普通权限
- **UserPrivilegeOrganizer** 版主权限
- **UserPrivilegeSuperuser** 站长权限

### 投稿类型设计
* **SubmissionTypeDoc**
* **SubmissionTypeAudio**
* **SubmissionTypeVideo**
  
### 用户数据结构 User
- **id** (number) ID
- **username** (string) 用户名 用于登录 
- **email** (string) email
- **privilege** (number) 权限
- **joined_at** (number) 加入时刻的 Unix 时间戳，单位为秒
- **nickname** (string) 昵称
- **bio** (string) 

### 用户数据结构 UserShort
仅包含 User 的 **id**, **username**, **privilege**，**nickname**

### 稿件数据结构 Submission
- **id** (number) ID
- **type** (number) 类型
- **resource** (string) URL
- **watchTimes** (number) 观看次数
- **pid** (number) 板块ID

### 板块数据结构
- **id** (number) ID
- **title** (string) 板块名称
- **owner** (number) 版主ID
- **startTime** (date) 创办时间
- **description** (string) 简介

## API设计

### 普适

状态码：
- 任何时候返回 401 表示未登录，应该重定向到登录页面，登录后返回当前页面。
- 任何时候返回 404 表示内容不存在或无权访问。
- 任何时候返回 429 表示请求频率过高，人类快速操作并不会导致此情形。
- 任何时候返回 500 表示服务器内部错误。
- 其余状态码由每个接入点各自规定。

### 注册 POST /signup

请求
- **username** (string) 登录名
- **password** (string) 不可逆哈希后的密码
- **email** (string) 清华邮箱
- **nickname** (string) 昵称

响应 200
- **err** ([number]) 空数组 []

响应 400
- **err** ([number]) 错误列表
	- **1**	用户名重复
	- **2** 邮箱重复
	- **-1** 任何一项信息过长、过短或包含不合法字符 —— 前端检查严格时不应出现此项

### 登录 POST /login

请求
- **username** (string) 登录名
- **password** (string) 不可逆哈希后的密码

响应 200
- 一个 UserShort
- 会设置 Cookie，以后的请求不必特殊处理

响应 400
- 空对象 {}
- 登录名或密码错误

### 退出登录 POST /logout

响应 200
- 空对象
- 会清除对应 Cookie，以后的请求不必特殊处理

### 当前帐号 GET /whoami

响应 200
- 一个 UserShort

响应 400
- 空对象 {}
- 未登录

### 个人主页 GET /user/{username}/profile

请求
# todo

响应
- **user** (User) 帐号信息

### 修改个人信息 POST /user/{username}/profile/edit

请求
- **nickname** (string) 昵称
- **bio** (string) 个性签名

响应 200
- 空对象 {}

响应 403 —— 前端处理正确时不应出现此项
- 空响应 Content-Length: 0
- 除站长外，不能修改其他人的个人信息

响应 400
- **err** ([number]) 错误列表
	- **-1** 任何一项信息过长、过短或包含不合法字符(例如邮箱格式错误) —— 前端检查严格时不应出现此项

### 修改密码 POST /user/{username}/password

请求
- **old** (string) 原密码
- **new** (string) 新密码

响应 200
- 空对象 {}

响应 400
- 空对象 {}
- 原密码错误

响应 403 —— 前端处理正确时不应出现此项
- 空响应 Content-Length: 0
- 除站长外，不能修改其他人的密码

### 修改头像 POST /user/{username}/avatar/upload

请求
- Content-Type: multipart/form-data
- **file** 一个图像文件，保留文件名，其后缀决定图像类型（image/\*\*\*）

响应 403 —— 前端处理正确时不应出现此项
- 空响应 Content-Length: 0
- 除站长外，不能修改其他人的头像

响应 400
- 空响应 Content-Length: 0
- 文件名后缀不是图像格式
- 或没有包含 **file** —— 前端处理正确时不应出现此项

### 获得头像 GET /user/{username}/avatar

响应 200
- 图像，将会设置 Content-Type，不必再加后缀

### 赋予或撤销站长权限 POST /user/{username}/promote

请求
- **set** (boolean) true 表示赋予权限，false 表示取消权限

响应 200
- 空对象 {}
- 无论前后权限是否变化，都正常返回

响应 403
- 空对象 {}
- 无站长权限 —— 前端检查严格时不应出现此项

### 按登录名搜索 GET /user_search/{username}

响应
- 若干 UserShort 组成的数组
- 登录名包含给定字符串的用户，至多返回 5 个

### 投稿 POST /submission/{username}/upload

请求

响应 200
- 空对象 {}

响应 403
- 空对象
- 无投稿权限 前端处理正确时不应出现此项
  
### 查看历史记录 GET /submission/{username}/history

请求
- **page** (number) 表示页数，从1开始
- **count** (number) 表示每页个数，大于0，默认10

响应 200
- 若干Submission组成的数组

### 查看投稿历史记录 GET /submission/{username}/upload/history

请求
- **page** (number) 表示页数，从1开始
- **count** (number) 表示每页个数，大于0，默认10

响应 200
- 若干Submission组成的数组


### 查看首页内容 GET /submission/hot
请求
- **page** (number) 表示页数，从1开始
- **count** (number) 表示每页个数，大于0，默认10

响应 200
- 若干Submission组成的数组

### 查看所有板块 GET /plate/list

请求
- **page** (number) 表示页数，从1开始
- **count** (number) 表示每页个数，大于0，默认10

响应 200
- 若干Plate组成的数组

### 加入该版块 POST /plate/{pid}/apply/{username}
请求

响应 200（这里我们先不做审核）

### 文件
:construction: 在这里我们需要提供视频、音频或者文档的下载 但是目前为止不知道怎么搞