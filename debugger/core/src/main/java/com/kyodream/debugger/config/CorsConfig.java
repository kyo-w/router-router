package com.kyodream.debugger.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
public class CorsConfig{
    @Bean(name = "corsFilter")
    public FilterRegistrationBean corsFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new CorsFilter());
        ArrayList<String> strings = new ArrayList<>();
        strings.add("/*");
        registrationBean.setUrlPatterns(strings);
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
