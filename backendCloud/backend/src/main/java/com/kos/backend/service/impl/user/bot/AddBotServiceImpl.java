package com.kos.backend.service.impl.user.bot;

import com.kos.backend.mapper.BotMapper;
import com.kos.backend.pojo.Bot;
import com.kos.backend.pojo.User;
import com.kos.backend.service.impl.utils.UserDetailsImpl;
import com.kos.backend.service.user.bot.AddBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.kos.backend.service.impl.utils.GetUserData.getUserData;

@Service
public class AddBotServiceImpl implements AddBotService {

    @Autowired
    private BotMapper botMapper;

    @Override
    public Map<String, String> add(Map<String, String> data) {
        User user = getUserData();

        String title = data.get("title");
        String description = data.get("description");
        String content = data.get("content");

        Map<String, String> map = new HashMap<>();
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
        Bot bot = new Bot(null, user.getId(), title, description, content, now, now);

        botMapper.insert(bot);
        map.put("message", "success");

        return map;
    }
}
