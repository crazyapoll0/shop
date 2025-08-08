package com.example.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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
        http.formLogin((it) -> it
                .loginPage("/members/login")
                .defaultSuccessUrl("/")
                .usernameParameter("email")
                .failureUrl("/members/login/error")
                );

        //CSRF 보호 기능 OFF
        //http.csrf(csrf -> csrf.disable());
       // http.csrf(AbstractHttpConfigurer::disable);

        //로그아웃 설정.
        http.logout((it)->it
            .logoutUrl("/members/logout")
            .logoutSuccessUrl("/")
        );
        return http.build(); //위에서 설정한 내용을 바탕으로 SecurityFilterChain 객체를 생성하여 반환
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); //해시 함수를 이용하여 암호화하여 저장
        // 단방향 암호화.(복호화 불가능).로그인할때마다 hash함수로 유사성 비교. (동일x).
    }
}
