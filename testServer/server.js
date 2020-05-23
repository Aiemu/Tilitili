const Koa = require('koa')
const Router = require('koa-router')
const bodyParser = require('koa-bodyparser')

const app = new Koa()
const router = new Router()

var account_1 = {
    "username": "1",
    "password": "1",
    "token": "Ayaka",
    "email": "list17@mails.tsinghua.edu.cn",
    "nickname": "渣渣辉",
    "privilege": 1,
    "ID": 1
        //     "joinAt": 1998.02.02,
}
var account_2 = {
    "username": "2",
    "password": "2",
    "token": "Chisato",
    "email": "list17@mails.tsinghua.edu.cn",
    "nickname": "渣渣辉",
    "privilege": 1,
    "ID": 2
        //     "joinAt": 1998.02.02,
}
var accounts = [account_1, account_2]
var loginAccount = undefined


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
        ctx.set('token', loginAccount.token)
    } else {
        ctx.body = {}
        ctx.response.status = 400
    }
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

app.use(bodyParser())
app.use(router.routes())
app.use(async(ctx, next) => {
    next()
})

app.listen(8000, () => {
    console.log("服务器已启动");
})