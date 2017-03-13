
import org.apache.log4j.Logger;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by LYY on 2017/3/12.
 */
@ServerEndpoint(value = "/Chat/{nickname}")
public class WebSocketServer {
    //private static Logger logger = Logger.getLogger(WebSocketServer.class);

    public Session getSession() {
        return session;
    }

    private Session session;
    private static int aliveCount = 0;
    private static ConcurrentHashMap<String, CopyOnWriteArraySet<WebSocketServer>> userRelation = new ConcurrentHashMap<>();

    public String getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(String currentRoom) {
        this.currentRoom = currentRoom;
    }

    private String currentRoom = "chatRoom";

    public boolean isInRoom() {
        return isInRoom;
    }

    public void setInRoom(boolean inRoom) {
        isInRoom = inRoom;
    }

    private boolean isInRoom = true;
    private static CopyOnWriteArraySet<WebSocketServer> aliveSocket = new CopyOnWriteArraySet();

    static {

        userRelation.put("chatRoom", new CopyOnWriteArraySet<WebSocketServer>());

    }


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    private String nickname;

    public WebSocketServer() {


    }

    @OnOpen
    public void onOpen(Session session, @PathParam(value = "nickname") String nickname) {
        System.out.print("连接建立");
        this.nickname = testNickname(nickname);
        this.session = session;
        userRelation.get("chatRoom").add(this);
        aliveSocket.add(this);
        addAliveCount();
        broadCast(String.format("用户:%s登陆 当前在线人数:%d", this.nickname, aliveCount), userRelation.get(currentRoom));
    }

    @OnClose
    public void onClose() {
        userRelation.get(currentRoom).remove(this);
        aliveSocket.remove(this);
        subAliveCount();
        //logger.info("用户退出 当前在线人数:" + aliveCount);
        broadCast(String.format("用户:%s退出 当前在线人数:%d", this.getNickname(), aliveCount), userRelation.get(currentRoom));
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        String mess = message.trim();
        System.out.print("用户命令" + mess);
        if (mess.startsWith(":add")) {
            String userName = mess.split(" ")[1];
            if (isInAliveSocket(userName)) {
                if (isInRoom) {
                    System.out.println("当前用户在大厅中 并且要添加用户在连接");
                    String roomName = String.format("room%03d", (int) (Math.random() * 100));
                    System.out.print("当前用户聊天房间:" + roomName);
                    this.currentRoom = roomName;
                    isInRoom = false;
                    userRelation.put(roomName, new CopyOnWriteArraySet<WebSocketServer>());
                    userRelation.get(roomName).add(this);
                    deleteUserFromAllRelationship(userRelation,userName);
                    userRelation.get(roomName).add(getWebSocketServerByName(userName));
                    getWebSocketServerByName(userName).setInRoom(false);
                    getWebSocketServerByName(userName).setCurrentRoom(roomName);


                }else{
                    System.out.println("当前用户在大厅中 并且要添加用户在连接");
                    deleteUserFromAllRelationship(userRelation,userName);
                    userRelation.get(currentRoom).add(getWebSocketServerByName(userName));
                    getWebSocketServerByName(userName).setInRoom(false);
                    getWebSocketServerByName(userName).setCurrentRoom(currentRoom);
                }
                session.getAsyncRemote().sendText("添加用户成功\r\n进入聊天室:"+currentRoom);
                getWebSocketServerByName(userName).getSession().getAsyncRemote().sendText("您被用户:"+
                        this.getNickname()+"加入聊天室:"+currentRoom);
            } else
                System.out.print("要添加用户不存在");
            return;
        } else if (mess.startsWith(":list")) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("用户列表:\r\n");
            for (WebSocketServer webSocketServer : userRelation.get(currentRoom)) {
                stringBuilder.append(webSocketServer.getNickname() + "\r\n");
            }
            session.getAsyncRemote().sendText(stringBuilder.toString());
            return;
        }

        broadCast(String.format("用户%s->%s", this.getNickname(), message), userRelation.get(currentRoom));
    }

    @OnError
    public void onError(Session session, Throwable error) {
        //logger.trace("发生错误", error);
        System.out.println("错误" + error.getMessage());
    }


    private synchronized static void addAliveCount() {
        aliveCount++;
    }

    private synchronized static void subAliveCount() {
        aliveCount--;
    }

    private synchronized static int getAliveCount() {
        return aliveCount;
    }

    private String testNickname(String nickname) {
        int x = (int) (Math.random() * 10000);
        return String.format("%s%04d", nickname, x);
    }


    private void deleteUserFromAllRelationship(ConcurrentHashMap<String, CopyOnWriteArraySet<WebSocketServer>> userRelation, String username) {
        for (CopyOnWriteArraySet<WebSocketServer> set : userRelation.values()) {
            for (WebSocketServer webSocketServer : set) {
                if (webSocketServer.getNickname().equals(username))
                    set.remove(webSocketServer);
            }
        }
    }

    private WebSocketServer getWebSocketServerByName(String name) {
        for (WebSocketServer webSocketServer : aliveSocket) {
            if (webSocketServer.getNickname().equals(name)) {
                return webSocketServer;
            }
        }
        return null;
    }

    private boolean isInAliveSocket(String userName) {
        for (WebSocketServer webSocketServer : aliveSocket) {
            if (webSocketServer.getNickname().equals(userName))
                return true;
        }
        return false;
    }

    public static void broadCast(String message, CopyOnWriteArraySet<WebSocketServer> area) {
        for (WebSocketServer client : area) {
            synchronized (client) {
                try {
                    client.session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    //logger.trace("广播 ioexception",e);
                    area.remove(client);
                    aliveSocket.remove(client);
                    try {
                        client.session.close();
                    } catch (IOException e1) {
                        //logger.trace("关闭失败 ioexception",e1);
                    }

                }
            }

        }
    }
}
