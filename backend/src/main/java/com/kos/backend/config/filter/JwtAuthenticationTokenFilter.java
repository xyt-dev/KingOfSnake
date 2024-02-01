package com.kos.backend.config.filter;

import com.kos.backend.mapper.UserMapper;
import com.kos.backend.pojo.User;
import com.kos.backend.service.impl.utils.UserDetailsImpl;
import com.kos.backend.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
// 1. @Component 注解表明这个类是一个 Spring 组件，Spring 将自动处理这个类的实例化和生命周期。
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    // 2. @Autowired 注解自动注入 UserMapper 对象，用于数据库中用户数据的操作。
    @Autowired
    private UserMapper userMapper;

    // 3. 重写 doFilterInternal 方法，这是过滤器中实际进行过滤操作的方法。
    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {

        // 4. 从请求头中获取名为 "Authorization" 的头部值，这通常是包含 JWT 的地方。
        String token = request.getHeader("Authorization");

        // 5. 检查 token 是否存在且格式正确（以 "Bearer " 开头）。
        if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
            // 继续过滤链的执行
            filterChain.doFilter(request, response);
            return;
        }

        // 6. 去除 JWT 前面的 "Bearer " 字样，得到纯净的 token。
        token = token.substring(7);

        String userid;
        try {
            // 7. 解析 JWT，从中提取出用户的 ID。
            Claims claims = JwtUtil.parseJWT(token);
            userid = claims.getSubject();
        } catch (Exception e) {
            // 8. 如果解析 JWT 失败（如过期或格式错误），抛出异常。
            throw new RuntimeException(e);
        }

        // 9. 使用用户 ID 从数据库中查询用户信息。
        User user = userMapper.selectById(Integer.parseInt(userid));

        // 10. 如果用户不存在，抛出异常。
        if (user == null) {
            throw new RuntimeException("用户名不存在");
        }

        // 11. 使用用户信息创建 UserDetailsImpl 实例，用于 Spring Security。
        UserDetailsImpl loginUser = new UserDetailsImpl(user);

        // 12. 创建一个不包含完全认证信息的 UsernamePasswordAuthenticationToken 对象。
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser, null, null);

        // 13. 将此 token 设置到 SecurityContext 中，这样在后续的处理流程中可以使用它。
        // 每一个 HTTP 请求都会被分配到一个新的线程来处理。这是由 Servlet 容器（如 Tomcat）负责的，它会为每个请求创建一个新的线程
        // 每次请求结束后线程也会结束，由于SecurityContext是线程局部的，所以在每次请求结束后，该线程的 SecurityContext 信息也会清理。
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // 继续过滤链的执行，即继续执行其他的过滤器或者最终的请求处理。
        filterChain.doFilter(request, response);
    }
}
