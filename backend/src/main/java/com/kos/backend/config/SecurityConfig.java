package com.kos.backend.config;

import com.kos.backend.config.filter.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
// 标记这个类为配置类，Spring容器将识别并使用它来配置应用程序。
@EnableWebSecurity
// 用于启用 Spring Security 的 Web 安全支持。它主要用于启动 Spring Security 的核心组件，例如 SecurityContextHolder，AuthenticationManager，AccessDecisionManager 等
// 若不添加该注解，将无法使用 Spring Security 提供的一些高级特性，例如自定义安全规则、自定义认证和授权策略等
public class SecurityConfig {
    // 这是配置类的声明。

    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    // 自动注入JwtAuthenticationTokenFilter。这是一个自定义过滤器，用于处理JWT（Json Web Token）认证。

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    // 定义一个Bean用于密码编码。BCryptPasswordEncoder是一种强哈希方法来存储密码。

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    // 定义一个Bean来获取AuthenticationManager。AuthenticationManager用于处理认证请求。

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(CsrfConfigurer::disable)
            // 禁用CSRF（跨站请求伪造）保护。由于使用token，不需要CSRF保护。

            .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 设置会话管理策略为无状态（STATELESS），因为使用token，所以不需要session。

            .authorizeHttpRequests((authz) -> authz
                .requestMatchers("/user/account/token/", "/user/account/register/").permitAll()
                // 对于某些请求路径（如登录和注册）允许所有用户访问，不需要认证。

                .requestMatchers(HttpMethod.OPTIONS).permitAll()
                // 允许所有预检（OPTIONS）请求，这常用于CORS（跨源资源共享）中。

                .anyRequest().authenticated()
                // 要求除了上述明确允许的请求外，所有其他请求都需要认证。
            )
            .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        // 在UsernamePasswordAuthenticationFilter之前添加自定义的jwtAuthenticationTokenFilter。

        return http.build();
        // 构建并返回SecurityFilterChain实例。(http 是继承了 AbstractSecurityBuilder<SecurityFilterChain> 的 HttpSecurity 实例)
    }
}
