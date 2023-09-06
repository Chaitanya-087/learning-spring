package com.learning.spring.social.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

import com.learning.spring.social.business.LoggedInUser;

@Configuration
public class SessionBeanConfig {
    @Bean
    @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
    LoggedInUser loggedInUser() {
        return new LoggedInUser();
    }
}
