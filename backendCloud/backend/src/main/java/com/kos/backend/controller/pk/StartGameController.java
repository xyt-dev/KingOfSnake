package com.kos.backend.controller.pk;

import com.kos.backend.service.pk.StartGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class StartGameController {
    @Autowired
    private StartGameService startGameService;

    @PostMapping("/pk/start/game/")
    public String startGame(@RequestParam MultiValueMap<String, String> data) {
        Integer user1Id = Integer.parseInt(Objects.requireNonNull(data.getFirst("user1_id")));
        Integer user1BotId = Integer.parseInt(Objects.requireNonNull(data.getFirst("user1_bot_id")));
        Integer user2Id = Integer.parseInt(Objects.requireNonNull(data.getFirst("user2_id")));
        Integer user2BotId = Integer.parseInt(Objects.requireNonNull(data.getFirst("user2_bot_id")));
        return startGameService.startGame(user1Id, user1BotId, user2Id, user2BotId);
    }
}
