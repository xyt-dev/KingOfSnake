package com.kos.backend.service.impl.user.bot;

import com.kos.backend.mapper.BotMapper;
import com.kos.backend.pojo.Bot;
import com.kos.backend.pojo.User;
import com.kos.backend.service.user.bot.RemoveBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.kos.backend.service.impl.utils.GetUserData.getUserData;

@Service
public class RemoveBotServiceImpl implements RemoveBotService {
    @Autowired
    private BotMapper botMapper;

    @Override
    public Map<String, String> remove(Map<String, String> data) {
        User user = getUserData();

        Integer bot_id = Integer.parseInt(data.get("bot_id"));
        Bot bot = botMapper.selectById(bot_id);

        Map<String, String> map = new HashMap<>();

        if(bot == null) {
            map.put("message", "Bot不存在");
            return map;
        }
        if(!bot.getUserId().equals(user.getId())) {
            map.put("message", "无权删除该Bot");
            return map;
        }

        botMapper.deleteById(bot_id);
        map.put("message", "success");

        return map;
    }
}
