package com.kos.backend.service.impl.pk;

import com.kos.backend.consumer.WebSocketServer;
import com.kos.backend.service.pk.StartGameService;
import org.springframework.stereotype.Service;

@Service
public class StartGameServiceImpl implements StartGameService {
    @Override
    public String startGame(Integer user1Id, Integer user1BotId, Integer user2Id, Integer user2BotId) {
        System.out.println(user1Id + "(" + user1BotId + ") VS " + user2Id + "(" + user2BotId + ")");
        WebSocketServer.startGame(user1Id, user1BotId, user2Id, user2BotId);
        return "start game success";
    }
}
