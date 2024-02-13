package com.kos.backend.service.impl.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kos.backend.mapper.UserMapper;
import com.kos.backend.pojo.User;
import com.kos.backend.service.user.account.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.QueryEval;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Map<String, String> register(String username, String password, String confirmedPassword) {
        Map<String, String> map = new HashMap<>();
        // 表单的对象数据被送入一个map，而 map.get() 尝试获取一个不存在的键值时，会返回 null
        if(username == null) { // 判断有无该参数
            map.put("message", "username is null");
            return map;
        }
        if(password == null || confirmedPassword == null) {
            map.put("message", "password is null");
            return map;
        }

        username = username.trim();
        // 密码首尾可以有空格

        if(username.isEmpty()) { // 判断内容是否为空
            map.put("message", "username is empty");
            return map;
        }
        if(username.length() > 100) {
            map.put("message", "username is too long");
            return map;
        }

        if(password.isEmpty()) {
            map.put("message", "password is empty");
            return map;
        }
        if(password.length() > 100 || confirmedPassword.length() > 100) {
            map.put("message", "password is too long");
            return map;
        }
        if(!password.equals(confirmedPassword)) {
            map.put("message", "password is not equal to confirmedPassword");
            return map;
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.eq("username", username);
        List<User> users = userMapper.selectList(queryWrapper);
        if (!users.isEmpty()) {
            map.put("message", "username is already exist");
            return map;
        }

        String encodedPassword = passwordEncoder.encode(password);
        String photo = "https://cdn77-pic.xvideos-cdn.com/videos/thumbs169l/6c/75/1e/6c751ed3fc0d8f5de092f4a476ade014-1/" +
                "6c751ed3fc0d8f5de092f4a476ade014.23.jpg";
        User user = new User(null, username, encodedPassword, photo);
        userMapper.insert(user);

        map.put("message", "success");
        return map;
    }
}
