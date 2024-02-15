package com.kos.backend.consumer;

import com.alibaba.fastjson.JSONObject;
import com.kos.backend.consumer.utils.Game;
import com.kos.backend.consumer.utils.JwtAuthentication;
import com.kos.backend.mapper.RecordMapper;
import com.kos.backend.mapper.UserMapper;
import com.kos.backend.pojo.User;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/websocket/{token}") // 注意不能以 / 结尾 (以 / 结尾表示目录，这里要表示端点，加 / 会导致异常)
public class WebSocketServer { // 一个连接对应一个 WebSocketServer 对象和一个线程
    // CopyOnWriteArraySet 是一个线程安全的集合，采用写时复制，写完替换原有数据(替换操作是原子的)，适用于读多写少的场景
    public static final ConcurrentHashMap<Integer, WebSocketServer> userSocketMap = new ConcurrentHashMap<>(); // 线程安全的 HashMap
    private static UserMapper userMapper;
    public static RecordMapper recordMapper;
    private static RestTemplate restTemplate;

    private User user;
    private Session session = null;
    private Game game = null;

    private final static String addPlayerUrl = "http://127.0.0.1:3001/player/add/";
    private final static String removePlayerUrl = "http://127.0.0.1:3001/player/remove/";

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }

    @Autowired
    public void setRecordMapper(RecordMapper recordMapper) {
        WebSocketServer.recordMapper = recordMapper;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        WebSocketServer.restTemplate = restTemplate;
    }

    // WebSocket连接打开时，WebSocket API会创建一个新的session对象，并自动传递给@OnOpen注解的方法。
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        // 建立连接
        this.session = session;
        Integer userId = JwtAuthentication.getUserId(token);
        this.user = userMapper.selectById(userId);
        if(this.user != null) { // 用户存在，token 正确
            userSocketMap.put(userId, this); // 根据 userId 存放
            System.out.println("Connection opened: " + user);
        } else {
            this.session.close();
        }
    }

    @OnClose
    public void onClose(){
        // 关闭连接
        System.out.println("Connection closed");
        if(this.user != null) {
            userSocketMap.remove(this.user.getId());
        }
    }

    public static void startGame(Integer user1Id, Integer user2Id) {
        User user1 = userMapper.selectById(user1Id);
        User user2 = userMapper.selectById(user2Id);

        if(user1 == null || user2 == null) {
            System.out.println("user not found: " + user1Id + " " + user2Id);
            return;
        }

        Game game = new Game(21, 20, 50, user1.getId(), user2.getId());
        game.createMap();

        // 线程安全的判断: if(userSocketMap.get(user1.getId()) != null) { userSocketMap.get(user1.getId()).game = game; }
        // userSocketMap.get(user1.getId()).game.PlayerA.userId = user1.getId() .PlayerB.userId = user2.getId();
        // userSocketMap.get(user2.getId()).game.PlayerA.userId = user1.getId() .PlayerB.userId = user2.getId();
        // 也就是说 已匹配的 userSocket 中的 game 中 PlayerA 或 PlayerB 必有一个是该 userSocket 对应的 userId，另一个是对手的 userId
        userSocketMap.computeIfPresent(user1.getId(), (key, value) -> {
            value.game = game;
            return value;
        });
        userSocketMap.computeIfPresent(user2.getId(), (key, value) -> {
            value.game = game;
            return value;
        });

        sendMatchingSuccessMessage(user1, user2, game);
        sendMatchingSuccessMessage(user2, user1, game);

        game.start();
    }

    private static void sendMatchingSuccessMessage(User user1, User user2, Game game) {
        JSONObject resp = new JSONObject();
        resp.put("event", "match-success");
        resp.put("opponent_username", user2.getUsername());
        resp.put("opponent_photo", user2.getPhoto());
        resp.put("gamemap", game.getG());
        resp.put("rows", game.getRows());
        resp.put("cols", game.getCols());
        resp.put("a_id", game.getPlayerA().getId());
        resp.put("a_sx", game.getPlayerA().getSx());
        resp.put("a_sy", game.getPlayerA().getSy());
        resp.put("b_id", game.getPlayerB().getId());
        resp.put("b_sx", game.getPlayerB().getSx());
        resp.put("b_sy", game.getPlayerB().getSy());

        // 线程安全的判断
        userSocketMap.computeIfPresent(user1.getId(), (key, value) -> {
            value.sendMessage(resp.toJSONString());
            return value;
        });
    }

    private void startMatching() {
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", user.getId().toString());
        data.add("rating", user.getRating().toString());
        System.out.println(data);
        restTemplate.postForObject(addPlayerUrl, data, String.class);
    }

    private void stopMatching() {
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", user.getId().toString());
        System.out.println(data);
        restTemplate.postForObject(removePlayerUrl, data, String.class);
    }

    private void move(Integer direction) {
        if(game.getPlayerA().getId().equals(user.getId())) {
            game.setNextStepA(direction);
        } else if(game.getPlayerB().getId().equals(user.getId())){
            game.setNextStepB(direction);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Message received: " + message);
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        if("start-matching".equals(event)) {
            startMatching();
        } else if ("stop-matching".equals(event)) {
            stopMatching();
        } else if ("move".equals(event)) {
            move(data.getInteger("direction"));
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessage(String message) {
        synchronized(this.session) {
            try {
                this.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
