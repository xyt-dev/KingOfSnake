package com.kos.backend.controller.user.bot;

import com.kos.backend.pojo.Bot;
import com.kos.backend.service.user.bot.GetBotListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GetBotListController {
    @Autowired
    private GetBotListService getBotListService;

    @GetMapping("/user/bot/getlist/")
    public List<Bot> getBotList() {
        return getBotListService.getList();
    }
}
