package com.zhourui.chatroom.config;


import jakarta.websocket.Session;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class SessionPool {

    private static Map<String, Session> userIdToSession = new ConcurrentHashMap<String, Session>();
    private static Map<String, String> sessionIdToUserId = new ConcurrentHashMap<String, String>();


    public static void add(String userId, Session session) {
        userIdToSession.put(userId, session);
        sessionIdToUserId.put(session.getId(), userId);
    }

    public static void remove(Session session) throws IOException {
        String userId = sessionIdToUserId.remove(session.getId());
        userIdToSession.remove(userId);
        sessionIdToUserId.remove(session.getId());
    }

    public static void sendMessage(Session session , String message) {
        String userId = sessionIdToUserId.get(session.getId());
        userIdToSession.get(userId).getAsyncRemote().sendText(message);
    }


    public static void sendMessage(Map<String, Object> params) {
        String toUserId = params.get("toUserId").toString();
        String fromUserId = params.get("fromUserId").toString();
        String msg  = params.get("msg").toString();
        msg = "来自" + fromUserId + "的消息" + toUserId;
        Session session = userIdToSession.get(toUserId);
        if (session != null) {
            session.getAsyncRemote().sendText(msg);
        }
    }
}
