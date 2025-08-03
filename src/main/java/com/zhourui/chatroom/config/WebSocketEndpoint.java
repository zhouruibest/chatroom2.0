package com.zhourui.chatroom.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import jakarta.websocket.Session;
import org.springframework.stereotype.Component;


@ServerEndpoint(value = "/websocket/{userId}")
@Component
public class WebSocketEndpoint {

    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        // 分解获取的参数,把参数信息，放入到session key中, 以方便后续使用
//		String queryString = session.getQueryString();
//		HashMap<String,String> maps = HttpContextUtils.parseQueryString(queryString);
//		String userId = maps.get("userId");

        // 把会话存入到连接池中
        System.out.println("onOpen: sessionId" + session.getId() + ", userId" + userId);
        this.session = session;
        SessionPool.add(userId, session);

    }

    /**
     * 关闭连接
     */
    @OnClose
    public void onClose(Session session) throws IOException {
        System.out.println("onClose: sessionId" + session.getId());
        SessionPool.remove(session);
        session.close();
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message
     *            客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        if (message.equalsIgnoreCase("ping")) {
            try {
                Map<String, Object> params = new HashMap<>();
                params.put("type", "pong");
                session.getBasicRemote().sendText(JSON.toJSONString(params));
                System.out.println("应答客户端的消息：" + JSON.toJSONString(params));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Map<String, Object> params = JSON.parseObject(message, new HashMap<String, Object>().getClass());
            SessionPool.sendMessage(params);
        }

    }

}
