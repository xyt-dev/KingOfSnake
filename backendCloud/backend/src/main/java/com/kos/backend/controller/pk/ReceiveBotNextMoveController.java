package com.kos.backend.controller.pk;

import com.kos.backend.service.pk.ReceiveBotNextMoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class ReceiveBotNextMoveController {
    @Autowired
    private ReceiveBotNextMoveService receiveBotNextMoveService;

    @PostMapping("/pk/receive/bot/nextmove/")
    public String receiveBotNextMove(@RequestParam MultiValueMap<String, String> data) {
        Integer userId = Integer.parseInt(Objects.requireNonNull(data.getFirst("user_id")));
        Integer direction = Integer.parseInt(Objects.requireNonNull(data.getFirst("direction")));
        return receiveBotNextMoveService.receiveBotNextMove(userId, direction);
    }
}
