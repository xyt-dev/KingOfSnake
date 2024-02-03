package com.kos.backend.controller.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kos.backend.mapper.UserMapper;
import com.kos.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Test
@RestController
public class UserController {

    @Autowired // 依赖注入
    UserMapper userMapper;

    @GetMapping("/user/all/")
    public List<User> getAll() {
        return userMapper.selectList(null);
    }

    @GetMapping("/user/account/register/")
    public List<User> getTest() {
        return userMapper.selectList(null);
    }

    @GetMapping("/user/{userId}/")
    public User getuser(@PathVariable int userId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", userId);
        return userMapper.selectById(userId);
    }

    @GetMapping("/user/add/{userId}/{username}/{password}/{photoUrl}/")
    public String addUser(
            @PathVariable int userId,
            @PathVariable String username,
            @PathVariable String password,
            @PathVariable String photoUrl
    ) {
        var encoder = new BCryptPasswordEncoder();
        var encodedPassword = encoder.encode(password);
        User user = new User(userId, username, encodedPassword, photoUrl);
        userMapper.insert(user);
        return "success" + user.toString();
    }

    @GetMapping("/user/delete/{userId}/")
    public String deleteUser(
            @PathVariable int userId
    ) {
        userMapper.deleteById(userId);
        return "success";
    }
}
