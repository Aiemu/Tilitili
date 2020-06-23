# 后端设计文档

## 基本架构

后端基于Spring Boot进行实现,

- 使用MyBatis+MySQL组合, 构造DAO, 将业务数据处理和实际数据库操作解耦.
- 使用Spring Boot提供的RestController, 对不同类型的请求分别处理.

## 后端服务启动流程

1. 首先需要在服务端(服务器或本地)安装mysql服务.
2. 将application.properties的`spring.datasource.username`和`spring.datasource.password`
设置为mysql某个用户的用户名和密码.
3. 登录mysql, 并执行一次`TableInit.sql`中的SQL语句, 进行数据库的初始化. 
4. 在Backend目录下运行命令`mvn spring-boot:run`.

## 数据库设计

为了满足应用的业务要求, 我们需要设计数据库. 根据我们应用的类型, 设计以下的表:

- User: 存储用户的信息
- Submission: 存储投稿信息
- Comment: 存储评论信息
- Plate: 存储板块信息
- PlateAuth: 存储用户对板块的权限
- Follow: 存储用户之间关注关系
- Favorite: 存储用户与投稿收藏关系
- Likes: 存储用户点赞关系
- History: 存储用户观看历史
- Message: 存储离线消息.

具体元素的设计可以见`model`的java类和`TableInit.sql`.

## API设计

API的设计请见[API设计文档](https://demo.codimd.org/4I10LJ06RM2naRtCc9Qr_w?both).

## RestController

根据API的设计, 我们以各个API开头作为分类, 设计不同的RestController:

- GeneralController: 负责处理一些通用请求, 例如登录注册, 静态文件上传等.
- UserController: 负责处理用户数据, 例如获取修改用户信息, 查看历史记录和投稿记录等.
- PlateController: 负责处理板块有关请求, 例如获取所有板块信息等.
- SubmissionController: 负责处理投稿有关的请求, 例如获取投稿详细内容, 投稿, 根据时间和热度获取
投稿列表等.

## 错误信息格式

为了让前端能更好地处理后端返回的错误信息, 后端统一了SpringBoot的错误信息和本应用规定的业务错误信息:

- `timestamp`: 发生错误的时间.
- `uri`: 发生错误时访问的API.
- `errorCode`: 错误码.
- `errorMessage`: 具体错误信息.

实现在`BusinessExceptionController`和`BusinessExceptionHandler`内.

## 其他

为了免去每次新增需要登陆才能使用的API都进行验证的麻烦, 后端实现了`@LoginAuth`注解, 具体为在
spring-boot的框架基础上构造一个拦截器, 每次收到请求, 都会检查该请求处理函数是否被
`@LoginAuth`修饰, 如果有, 则检查是否有登录信息, 没有登录则会拒绝访问.