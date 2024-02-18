package com.kob.botrunningsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Arrays;
import java.util.List;

@Configuration
// 用于启用 Spring Security 的 Web 安全支持。它主要用于启动 Spring Security 的核心组件，例如 SecurityContextHolder，AuthenticationManager，AccessDecisionManager 等
// 若不添加该注解，将无法使用 Spring Security 提供的一些高级特性，例如自定义安全规则、自定义认证和授权策略等
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 指定允许的IP地址
        List<String> allowedIps = Arrays.asList("127.0.0.1", "192.168.31.157");
        // 指定允许的URL路径
        String[] allowedUrls = {"/bot/add/", "/error"}; // 注意加 “/error" 让Spring Security不拦截错误页面

        RequestMatcher customMatcher = request -> {
            boolean ipMatch = allowedIps.stream().anyMatch(ip -> new IpAddressMatcher(ip).matches(request));
            if (!ipMatch) {
                return false; // 如果IP地址不匹配，则直接拒绝
            }

            String requestUrl = request.getRequestURI();

            // 检查请求的URL是否为允许的路径之一
            for (String url : allowedUrls) {
                if (requestUrl.startsWith(url)) {
                    return true; // 如果是允许的路径，且IP地址匹配，则允许访问
                }
            }

            System.out.println("Not allowed");
            return false; // 如果不是指定的路径，拒绝访问
        };

        http.csrf(CsrfConfigurer::disable) // 必须加这个，否则后端无法访问
                .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.OPTIONS).permitAll() // 允许所有预检（OPTIONS）请求
                .requestMatchers(customMatcher).permitAll() // 使用自定义RequestMatcher
                .anyRequest().authenticated() // 其他所有请求都需要认证
        );

        return http.build();
    }
}