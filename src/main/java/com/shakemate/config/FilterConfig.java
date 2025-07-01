package com.shakemate.config;

import com.shakemate.filter.LoginFilter;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<Filter> loginFilterRegistration() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new LoginFilter()); // 註冊自訂的 LoginFilter
        registrationBean.addUrlPatterns(
                "/user/*",     // 可根據實際需要改為攔截哪些 URL
                "/testlogin/*",
                "/notifications/*"  // 添加通知模組URL過濾
        );
        registrationBean.setName("loginFilter"); // Filter 名稱
        registrationBean.setOrder(1); // 執行順序，數字越小越先執行

        return registrationBean;
    }

}
