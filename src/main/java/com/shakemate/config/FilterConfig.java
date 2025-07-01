package com.shakemate.config;

import com.shakemate.filter.LoginFilter;
import com.shakemate.user.filter.UserLoginFilter;
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
                "/testlogin/*" // 保留原有的 testlogin 路徑

        );
        registrationBean.setName("loginFilter"); // Filter 名稱
        registrationBean.setOrder(2); // 執行順序，數字越小越先執行

        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<Filter> userLoginFilterRegistration() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new UserLoginFilter()); // 註冊新的 UserLoginFilter
        registrationBean.addUrlPatterns(
                "/user/*", // 用戶相關功能
                "/match/*", // 配對功能
                "/chatroom/*", // 聊天室功能
                "/servicecase/*", // 服務案例（需要登入的部分）
                "/news/*", // 新聞（需要登入的部分）
                "/shshop/*" // 商店功能（需要登入的部分）
        );
        registrationBean.setName("userLoginFilter"); // Filter 名稱
        registrationBean.setOrder(1); // 執行順序，數字越小越先執行

        return registrationBean;
    }

}
