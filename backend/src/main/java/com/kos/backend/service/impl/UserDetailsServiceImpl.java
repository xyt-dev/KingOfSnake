package com.kos.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kos.backend.mapper.UserMapper;
import com.kos.backend.pojo.User;
import com.kos.backend.service.impl.utils.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// 在Spring Security中，UserDetailsService接口的loadUserByUsername方法被用来从数据库或其他数据源加载用户信息
// 这个方法需要返回一个UserDetails对象，该对象包含了用户的用户名、密码、权限等信息

// 以下是UserDetails对象的主要内容：
// username：用户名，用于身份验证。
// password：用户密码，用于身份验证。
// authorities：用户的权限集合，用于权限控制。
// accountNonExpired：账户是否未过期，用于账户管理。
// accountNonLocked：账户是否未被锁定，用于账户管理。
// credentialsNonExpired：凭证（密码）是否未过期，用于账户管理。
// enabled：账户是否启用，用于账户管理。

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new RuntimeException("用户名或密码哦哦哦");
        }
        return new UserDetailsImpl(user);
    }
}
