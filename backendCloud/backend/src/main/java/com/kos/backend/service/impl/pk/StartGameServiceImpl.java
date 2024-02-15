package com.kos.backend.service.impl.pk;

import com.kos.backend.consumer.WebSocketServer;
import com.kos.backend.service.pk.StartGameService;
import org.springframework.stereotype.Service;

@Service
public class StartGameServiceImpl implements StartGameService {
    @Override
    public String startGame(Integer user1Id, Integer user2Id) {
        System.out.println("start game: "  + user1Id + " vs " + user2Id);
        WebSocketServer.startGame(user1Id, user2Id);
        return "start game success";
    }
}
