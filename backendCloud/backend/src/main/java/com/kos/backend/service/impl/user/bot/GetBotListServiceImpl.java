package com.kos.backend.service.impl.user.bot;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kos.backend.mapper.BotMapper;
import com.kos.backend.pojo.Bot;
import com.kos.backend.pojo.User;
import com.kos.backend.service.user.bot.GetBotListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.kos.backend.service.impl.utils.GetUserData.getUserData;

@Service
public class GetBotListServiceImpl implements GetBotListService {
    @Autowired
    private BotMapper botMapper;

    @Override
    public List<Bot> getList() {
        User user = getUserData();
        QueryWrapper<Bot> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId());

        return botMapper.selectList(queryWrapper);
    }
}
