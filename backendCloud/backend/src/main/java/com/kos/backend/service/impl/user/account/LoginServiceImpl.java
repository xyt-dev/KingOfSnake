package com.kos.backend.service.impl.user.account;

import com.kos.backend.pojo.User;
import com.kos.backend.service.impl.utils.UserDetailsImpl;
import com.kos.backend.service.user.account.LoginService;
import com.kos.backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public Map<String, String> getToken(String username, String password) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);

        // 使用配置的 UserDetailsService（UserDetailsServiceImpl）和密码编码器来验证提交的用户名和密码。
        // 如果验证成功，authenticate方法会返回一个已验证的Authentication对象，否则会抛出异常 AuthenticationException。
        // AuthenticationException 通常会被 Spring Security 的过滤器链自动捕获并处理
        Authentication authenticate = authenticationManager.authenticate(authenticationToken); // 登录验证
        // AuthenticationManager 的 authenticate 方法的工作流程如下：
        // AuthenticationManager 接收到一个未经认证的 Authentication 实例（通常是 UsernamePasswordAuthenticationToken 实例，包含用户名和密码）
        // AuthenticationManager 将 Authentication 实例传递给内部的 AuthenticationProvider 实例进行认证。
        // AuthenticationProvider 使用 UserDetailsService 加载用户详细信息，并使用密码编码器（PasswordEncoder）比较提供的密码和存储的密码是否匹配。
        // 如果用户认证成功，AuthenticationProvider 返回一个已认证的 Authentication 实例（通常是 UsernamePasswordAuthenticationToken 实例，包含用户详细信息和授权信息）。
        // 如果所有的 AuthenticationProvider 都无法认证用户，AuthenticationManager 将抛出 AuthenticationException。

        User user = getUser(authenticate);

        String jwt = JwtUtil.createJWT(user.getId().toString());

        Map<String, String> map = new HashMap<>();
        map.put("message", "success");
        map.put("token", jwt);

        return map;
    }

    private static User getUser(Authentication authenticate) {
        // 在 Spring Security 中，"principal" 通常指的是已经通过身份验证的用户。
        // Authentication 对象中的 getPrincipal() 方法用于获取这个用户的详细信息。
        // 这里的UserDetails 对象是在 UserDetailsServiceImpl.loadUserByUsername() 中创建并返回的。
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticate.getPrincipal();
        return loginUser.getUser();
    }
}
