package com.kos.backend.service.impl.pk;

import com.kos.backend.consumer.WebSocketServer;
import com.kos.backend.consumer.utils.Game;
import com.kos.backend.service.pk.ReceiveBotNextMoveService;
import org.springframework.stereotype.Service;

@Service
public class ReceiveBotNextMoveServiceImpl implements ReceiveBotNextMoveService {

    @Override
    public String receiveBotNextMove(Integer userId, Integer direction) {
        System.out.println("receive bot move: " + userId + " " + direction);
        if (WebSocketServer.userSocketMap.get(userId) != null) {
            Game game = WebSocketServer.userSocketMap.get(userId).game;
            if (game != null) {
                if(game.getPlayerA().getId().equals(userId)) {
                    game.setNextStepA(direction);
                } else if(game.getPlayerB().getId().equals(userId)) {
                    game.setNextStepB(direction);
                }
            }
        }
        return "receive bot move success";
    }
}
