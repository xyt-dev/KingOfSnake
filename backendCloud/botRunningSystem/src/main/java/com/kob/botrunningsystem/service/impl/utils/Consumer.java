package com.kob.botrunningsystem.service.impl.utils;

import com.kob.botrunningsystem.utils.BotInterface;
import org.joor.Reflect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
public class Consumer extends Thread {
    private Bot bot;
    private static RestTemplate restTemplate;
    private final static String receiveBotNextMoveUrl = "http://127.0.0.1:3000/pk/receive/bot/nextmove/";

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        Consumer.restTemplate = restTemplate;
    }

    public void startTimeout(long timeout, Bot bot) {
        this.bot = bot;
        this.start();

        try {
            this.join(timeout); // 最多等待 timeout 毫秒
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            this.interrupt(); // 无论如何都中断该线程
        }
    }

    private String addUid(String code, String uid) { // 在 BotCode 中的 Bot 类名后添加 uid
        int k = code.indexOf(" implements com.kob.botrunningsystem.utils.BotInterface");
        return code.substring(0, k) + uid + code.substring(k);
    }

    @Override
    public void run() {
        UUID uuid = UUID.randomUUID();
        String uid = uuid.toString().substring(0, 8);
        BotInterface botInstance = Reflect.compile(
                "com.kob.botrunningsystem.utils.Bot" + uid, // 同名类只会创建一个，需要加上一个随机字符串
                addUid(bot.getBotCode(), uid)
        ).create().get();

        Integer direction = botInstance.nextMove(bot.getInput());
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", bot.getUserId().toString());
        data.add("direction", direction.toString());
        restTemplate.postForObject(receiveBotNextMoveUrl, data, String.class);
    }
}
