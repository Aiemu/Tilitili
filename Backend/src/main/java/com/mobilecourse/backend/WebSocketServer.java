package com.mobilecourse.backend;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.controllers.CommonController;
import com.mobilecourse.backend.dao.MessageDao;
import com.mobilecourse.backend.dao.UserDao;
import com.mobilecourse.backend.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

@ServerEndpoint("/websocket/{uid}/{token}")
@Component
public class WebSocketServer {

    private final Logger LOG = LoggerFactory.getLogger(WebSocketServer.class);
    public static Hashtable<Integer, WebSocketServer> getWebSocketTable() {
        return webSocketTable;
    }
    public static Hashtable<Integer, String> getVerifyMap() { return verifyMap; }

    private static final Hashtable<Integer, String> verifyMap = new Hashtable<>();
    private static final Hashtable<Integer, WebSocketServer> webSocketTable = new Hashtable<>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    //用于标识客户端的sid
    private Integer uid;
    private boolean isValid;

    private static ApplicationContext applicationContext;
    private MessageDao messageDao;
    private UserDao userDao;
    public static void setApplicationContext(ApplicationContext applicationContext) {
        WebSocketServer.applicationContext = applicationContext;
    }

    //推荐在连接的时候进行检查，防止有人冒名连接
    @OnOpen
    public void onOpen(Session session, @PathParam("uid") Integer uid,
                       @PathParam("token") String token) {
        messageDao = applicationContext.getBean(MessageDao.class);
        userDao = applicationContext.getBean(UserDao.class);

        this.session = session;
        this.uid = uid;
        this.isValid = false;

        String verifyToken = verifyMap.get(uid);
        if (!token.equals(verifyToken)) {
            //验证不通过
            LOG.warn(String.format("Authorization failure in WebSocket (%d)", uid));
            try {
                JSONObject errorMessageJSON = new JSONObject();
                errorMessageJSON.put("type", 0);
                errorMessageJSON.put("message", "Authorization failure.");
                sendMessage(errorMessageJSON.toJSONString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            throw new RuntimeException();
        }
        //验证成功, 建立websocket
        if (webSocketTable.containsKey(uid)) {
            //重复登录, 关闭已经登陆的websocket.
            try {
                LOG.warn(String.format("Duplicate websocket (%d) and will be removed.", uid));
                webSocketTable.get(uid).session.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //加入新的websocket到table中.
        this.isValid = true;
        webSocketTable.put(this.uid, this);
        // 获取所有的离线信息
        List<Message> messages = messageDao.getOfflineMessages(uid);
        messageDao.clearOfflineMessages(uid);
        try {
            for (Message m: messages) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("uid", m.getSrcUid());
                jsonObject.put("type", m.getContent());
                jsonObject.put("content", m.getContent());
                sendMessage(jsonObject.toJSONString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOG.info(String.format("Connect with uid (%s)", this.uid));
    }

    // 在关闭连接时移除对应连接
    @OnClose
    public void onClose() {
        LOG.info(String.format("Disconnect with uid (%s)", this.uid));
        webSocketTable.remove(this.uid);
    }

    // 收到消息时候的处理
    @OnMessage
    public void onMessage(String message, Session session) {
        messageDao = applicationContext.getBean(MessageDao.class);
        userDao = applicationContext.getBean(UserDao.class);

        JSONObject jsonMessage = JSONObject.parseObject(message);
        JSONObject jsonObject = new JSONObject();
        try {
            if (!isValid) {
                JSONObject errorMessageJSON = new JSONObject();
                errorMessageJSON.put("type", 0);
                errorMessageJSON.put("message", "Authorization failure.");
                sendMessage(errorMessageJSON.toJSONString());
                session.close();
            }
            //获取发送方和接收方的uid.
            Integer destUid = jsonMessage.getInteger("uid");
            Integer srcUid = this.uid;
            if (webSocketTable.containsKey(destUid)) {
                //当前目标用户在线, 可以直接发送.
                jsonObject.put("uid", srcUid);
                jsonObject.put("type", 0);
                jsonObject.put("content", jsonMessage.getString("content"));
                webSocketTable.get(destUid).sendMessage(jsonObject.toJSONString());
            } else {
                Message offlineMessage = new Message();
                offlineMessage.setSrcUid(srcUid);
                offlineMessage.setDestUid(destUid);
                offlineMessage.setType(0);
                offlineMessage.setContent(jsonMessage.getString("content"));
                messageDao.putMessage(offlineMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
        webSocketTable.remove(this.uid);
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }
}
