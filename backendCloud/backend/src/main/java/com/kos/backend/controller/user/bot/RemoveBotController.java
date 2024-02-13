package com.kos.backend.controller.user.bot;

import com.kos.backend.service.user.bot.RemoveBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RemoveBotController {
    @Autowired
    private RemoveBotService removeBotService;

    @PostMapping("/user/bot/remove/")
    public Map<String, String> remove(@RequestParam Map<String, String> map) {
        return removeBotService.remove(map);
    }
}
