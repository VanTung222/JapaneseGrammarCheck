package com.example.grammar.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@WebFilter("/*")
public class UTF8Filter implements Filter {
    
    private static final Logger logger = Logger.getLogger(UTF8Filter.class.getName());
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("UTF8Filter initialized");
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        
        // Thiết lập encoding UTF-8 cho tất cả request và response
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
        
        // Add headers for UTF-8 support
        resp.setHeader("Content-Type", "text/html; charset=UTF-8");
        
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        logger.info("UTF8Filter destroyed");
    }
}
