package com.example.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
//위 어노테이션에 달린 클래스에 @Bean 어노테이션이 붙은 메서드를
//등록하면 해당 메서드의 반환 값이 스프링 빈으로 등록됨
@EnableWebSecurity
public class SecurityConfig {

    /* 스프링 시큐리티 필터 체인 */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        /*
        http.설정1
        http.설정2
        http.설정3
         */
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
