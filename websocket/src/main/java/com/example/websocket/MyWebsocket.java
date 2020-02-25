package com.example.websocket;

import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * create by Daniel ON 2020/2/25  11:51
 */
@Component
@ServerEndpoint(value = "/websocket/{user}")
public class MyWebsocket {
    //online number
    public static AtomicInteger atomicInteger = new AtomicInteger(0);
    //websocket list
    public static List<MyWebsocket> webSockets = new CopyOnWriteArrayList<MyWebsocket>();
    //session
    private Session session;
    //user
    private String user;

    @OnOpen
    public void onOpen(Session session, @PathParam("user") String user){
        if (null == user || "".equals(user)) {
            try {
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        atomicInteger.incrementAndGet();
        for (MyWebsocket webSocket : webSockets) {
            if (user.equals(webSocket.user)){
                try {
                    session.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        this.session = session;
        this.user = user;
        webSockets.add(this);
        System.out.println("有新连接加入！ 当前在线人数" + atomicInteger.get());
    }

    @OnClose
    public void onClose() {
        atomicInteger.decrementAndGet();
        webSockets.remove(this);
        System.out.println("有连接关闭！ 当前在线人数" + atomicInteger.get());
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("user") String user) {
        System.out.println("来自" + user + "消息：" + message);
        pushMessage(user, message, null);
    }

    public void sendMessage(String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void pushMessage(String user,String message,String uuid){
        if (null == uuid || "".equals(uuid)) {
            for (MyWebsocket webSocket : webSockets) {
                webSocket.sendMessage(user + ":"+message);
            }
        } else {
            for (MyWebsocket webSocket : webSockets) {
                if (uuid.equals(webSocket.user)) {
                    webSocket.sendMessage(message);
                }
                }
        }

    }
}
