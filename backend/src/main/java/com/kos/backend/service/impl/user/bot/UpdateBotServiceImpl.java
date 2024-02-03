package com.kos.backend.service.impl.user.bot;

import com.kos.backend.mapper.BotMapper;
import com.kos.backend.pojo.Bot;
import com.kos.backend.pojo.User;
import com.kos.backend.service.user.bot.UpdateBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.kos.backend.service.impl.utils.GetUserData.getUserData;

@Service
public class UpdateBotServiceImpl implements UpdateBotService {

    @Autowired
    private BotMapper botMapper;

    @Override
    public Map<String, String> update(Map<String, String> data) {
        User user = getUserData();

        int bot_id = Integer.parseInt(data.get("bot_id")); // data.get("bot_id") is a string

        Map<String, String> map = new HashMap<>();

        Bot bot = botMapper.selectById(bot_id);
        if(bot == null) {
            map.put("message", "Bot不存在");
            return map;
        }
        if(!bot.getUserId().equals(user.getId())) {
            map.put("message", "无权修改该Bot");
            return map;
        }

        String title = data.get("title");
        String description = data.get("description");
        String content = data.get("content");

        if(title == null || title.isEmpty()) {
            map.put("message", "标题不能为空");
            return map;
        }
        if(title.length() > 100) {
            map.put("message", "标题长度过长");
            return map;
        }
        if(description == null || description.isEmpty()) {
            description = "未添加描述内容"; // 默认内容
        }
        if(description.length() > 1000) {
            map.put("message", "描述长度过长");
            return map;
        }
        if(content == null || content.isEmpty()) {
            map.put("message", "代码内容不能为空");
            return map;
        }
        if(content.length() > 10000) {
            map.put("message", "代码内容长度过长");
            return map;
        }

        Date now = new Date();
        Bot updatedBot = new Bot(bot.getId(), user.getId(), title, description, content, bot.getRating(), bot.getCreateTime(), now);

        botMapper.updateById(updatedBot);
        map.put("message", "success");

        return map;
    }
}
