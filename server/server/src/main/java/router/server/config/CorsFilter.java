package router.server.config;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CorsFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String orignalHeader = ((HttpServletRequest) servletRequest).getHeader("Origin");
        if (orignalHeader != null) {
            ((HttpServletResponse) servletResponse).addHeader("Access-Control-Allow-Origin", orignalHeader);
            ((HttpServletResponse) servletResponse).addHeader("Access-Control-Allow-Credentials", "true");
            ((HttpServletResponse) servletResponse).addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
            ((HttpServletResponse) servletResponse).addHeader("Access-Control-Allow-Headers", ((HttpServletRequest) servletRequest).getHeader("Access-Control-Request-Headers"));
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
