package com.kos.backend.config;

import jakarta.servlet.Filter;
import org.springframework.context.annotation.Configuration;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;


// 在 Http 报头添加 "Access-Control-Allow-Origin" 等内容 从而允许 CORS(Cross Origin Resources Share) 允许跨域请求

@Configuration
// 定义一个名为CorsConfig的类，它实现了Filter接口，用于拦截和处理传入的HTTP请求。
public class CorsConfig implements Filter {

    // 实现Filter接口中的doFilter方法。每个HTTP请求都会通过这个方法。
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        // 将ServletResponse强制转换为HttpServletResponse，以便设置HTTP相关的响应头。
        HttpServletResponse response = (HttpServletResponse) res;
        // 将ServletRequest强制转换为HttpServletRequest，以便获取请求头信息。
        HttpServletRequest request = (HttpServletRequest) req;

        // 获取请求头中的Origin字段，这个字段指明了发起请求的源（例如http://example.com）
        String origin = request.getHeader("Origin");
        // 如果Origin字段不为空，将它的值添加到响应头Access-Control-Allow-Origin中，
        // 这允许与请求的源进行跨域通信。
        if (origin != null) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        }

        // 获取请求头中的Access-Control-Request-Headers字段，这个字段指明了请求想要使用的HTTP头。
        String headers = request.getHeader("Access-Control-Request-Headers");
        // 如果请求头中包含Access-Control-Request-Headers，则将这些头信息添加到
        // Access-Control-Allow-Headers和Access-Control-Expose-Headers响应头中，
        // 允许这些自定义的HTTP头在跨域请求中使用。
        if (headers != null) {
            response.setHeader("Access-Control-Allow-Headers", headers);
            response.setHeader("Access-Control-Expose-Headers", headers);
        }

        // 设置Access-Control-Allow-Methods响应头为*，允许所有的HTTP方法在跨域请求中使用。
        response.setHeader("Access-Control-Allow-Methods", "*");
        // 设置Access-Control-Max-Age响应头，定义了预检请求的结果能够被缓存多长时间（秒）。
        response.setHeader("Access-Control-Max-Age", "3600");
        // 设置Access-Control-Allow-Credentials响应头为true，允许前端在跨域请求中携带证书（如Cookies）。
        response.setHeader("Access-Control-Allow-Credentials", "true");

        // 继续过滤链中的下一个Filter，这是Filter接口的一个要求，
        // 保证请求和响应能够继续传递到应用程序中的其他部分。
        chain.doFilter(request, response);
    }

    // init方法用于Filter的初始化。在这个方法中，我们没有任何初始化代码。
    @Override
    public void init(FilterConfig filterConfig) {
        // 通常，您可以在此处初始化一些资源，但在此示例中，此方法为空。
    }

    // destroy方法在销毁Filter时被调用。可以用来释放资源。
    @Override
    public void destroy() {
        // 在这个例子中，没有需要清理的资源，所以方法体为空。
    }

}