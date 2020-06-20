const Koa = require('koa')
const Router = require('koa-router')
const bodyParser = require('koa-bodyparser')
const multer = require('koa-multer');

const app = new Koa()
const router = new Router()


var account_1 = {
    "username": "1",
    "password": "1",
    "token": "Ayaka",
    "email": "list17@mails.tsinghua.edu.cn",
    "nickname": "渣渣辉",
    "privilege": 1,
    "ID": 1,
    "joinAt": 3030303030,
}
var account_2 = {
    "username": "2",
    "password": "2",
    "token": "Chisato",
    "email": "list17@mails.tsinghua.edu.cn",
    "nickname": "渣渣辉",
    "privilege": 1,
    "ID": 2,
    "joinAt": 3030303030,
}

var plate1 = {
    "id": "1",
    "title": "软件学院",
    "startTime": 3030303030,
    "owner": 1,
    "description": "清华大学软件学院简介"
}

var submission_1 = {
    "id": 1,
    "plate": plate1,
    "type": 1,
    "resource": "123",
    "title": "庆祝清华大学109年校庆",
    "introduction": "庆祝清华大学校庆",
    "submissionTime": 1590600906,
    "watchTimes": 20,
    "likes": 1,
    "post_time": 59
}

var submission_2 = {
    "id": 2,
    "plate": plate1,
    "type": 1,
    "resource": "123",
    "title": "庆祝清华大学110年校庆",
    "introduction": "庆祝清华大学校庆",
    "submissionTime": 1590600906,
    "watchTimes": 20,
    "likes": 1,
    "post_time": 59
}

var submission_3 = {
    "id": 2,
    "plate": plate1,
    "type": 1,
    "resource": "123",
    "title": "庆祝清华大学111年校庆",
    "introduction": "庆祝清华大学校庆",
    "submissionTime": 1590600906,
    "watchTimes": 20,
    "likes": 1,
    "post_time": 59
}


var accounts = [account_1, account_2]
var loginAccount = undefined

var submission_list = [submission_1, submission_1, submission_1, submission_1, submission_1, submission_2, submission_2, submission_2, submission_2, submission_2, submission_3, submission_3, submission_3, submission_3, submission_3]

var plate_list = [plate1, plate1, plate1, plate1, plate1, plate1, plate1, plate1, plate1, plate1, plate1, plate1]

// 用户登录
router.post("/login", async(ctx, next) => {
    loginAccount = accounts.find(x => (x.username == ctx.request.body['username'] && (x.password == ctx.request.body['password'])))
    if (loginAccount) {
        ctx.body = {
            "id": loginAccount.ID,
            "username": loginAccount.username,
            "nickname": loginAccount.nickname,
            "privilege": loginAccount.privilege
        }
        ctx.response.status = 200
        ctx.cookies.set("JSESSIONID", "123")
        ctx.set('token', loginAccount.token)
    } else {
        ctx.body = {}
        ctx.response.status = 400
    }
    next()
})

// 用户注册
router.post("/signup", async(ctx, next) => {
    username = ctx.request.body['username']
    password = ctx.request.body['password']
    email = ctx.request.body['email']
    nickname = ctx.request.body['nickname']
    console.log(username, password, email, nickname)
    ctx.body = {}
    ctx.response.status = 200
    next()
})

router.post("/user/:username/password", async(ctx, next) => {
    let userAccount = accounts.find(x => x.username == ctx.params.username)
    if (userAccount && userAccount.password == ctx.request.body['old']) {
        userAccount.password = ctx.request.body['new']
        ctx.body = {}
        ctx.response.status = 200
    } else {
        ctx.body = {}
        ctx.response.status = 400
    }
    next()
})

router.get("/submission/hot", async(ctx, next) => {
    page = ctx.request.query['page']
    count = ctx.request.query['count']
    ctx.header.set
    ctx.body = {
        "currentPage": parseInt(page),
        "pageSize": submission_list.slice((page - 1) * count, page * count).length,
        "totalPage": Math.ceil(submission_list.length / count),
        "totalCount": submission_list.length,
        "list": submission_list.slice((page - 1) * count, page * count)
    }
    ctx.response.status = 200
    next()
})

router.get("/submission/user", async(ctx, next) => {
    page = ctx.request.query['page']
    count = ctx.request.query['count']
    ctx.header.set
    ctx.body = {
        "currentPage": parseInt(page),
        "pageSize": submission_list.slice((page - 1) * count, page * count).length,
        "totalPage": Math.ceil(submission_list.length / count),
        "totalCount": submission_list.length,
        "list": submission_list.slice((page - 1) * count, page * count)
    }
    ctx.response.status = 200
    next()
})

router.get("/plate", async(ctx, next) => {
    page = ctx.request.query['page']
    count = ctx.request.query['count']
    ctx.body = {
        "currentPage": parseInt(page),
        "pageSize": plate_list.slice((page - 1) * count, page * count).length,
        "totalPage": Math.ceil(plate_list.length / count),
        "totalCount": plate_list.length,
        "list": plate_list.slice((page - 1) * count, page * count)
    }
    ctx.response.status = 200
    next()
})

router.post('/upload', async function(ctx, next) {
    ctx.body = {
        "uri": "http://www.1honeywan.com/dachshund/image/7.21/7.21_3_thumb.JPG"
    }
    ctx.response.status = 200
    next()
});

router.get('/user/profile/info/1', async function(ctx, next) {
    console.log("/user/profile/info/:id已被访问")
    ctx.body = {
        "email": "list17@mails.tsinghua.edu.cn",
        "nickname": "list",
        "department": "软件学院",
        "organization": "天文协会",
        "joinAt": 1592413723,
        "bio": "大家好",
        "avatar": "123"
    }
    ctx.response.status = 200
    next()
})


router.get('/checklogin', async function(ctx, next) {
    ctx.response.status = 401
    next()
})

app.use(bodyParser())
app.use(router.routes())
app.use(async(ctx, next) => {
    next()
})

app.listen(8000, () => {
    console.log("服务器已启动");
})