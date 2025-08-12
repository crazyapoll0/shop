package com.example.shop.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
//위 어노테이션에 달린 클래스에 @Bean 어노테이션이 붙은 메서드를
//등록하면 해당 메서드의 반환 값이 스프링 빈으로 등록됨
@EnableWebSecurity
public class SecurityConfig {
    //화면을 정상적으로 그리는데 필요한 정적인 자원들 허용
    //특별히 보안적인 고려사항이 없는 웹 전용 자원들

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

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

        // 상품 등록 페이지는 ADMIN 계정만 접근 가능하도록 설정
        // URL 접근 권한 설정
        // - "/" 및 일부 경로는 모두 허용
        // - "/admin/**" 경로는 ADMIN 역할만 접근 가능
        // - 그 외의 모든 요청은 인증된 사용자만 접근 가능
        http.authorizeHttpRequests(
                auth -> auth
                        .requestMatchers("/", "/members/**", "/item/**", "/images/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
        );
        
        http.exceptionHandling((e)-> e
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                        .accessDeniedHandler(new CustomAccessDenitedHandler())
                );

        return http.build(); //위에서 설정한 내용을 바탕으로 SecurityFilterChain 객체를 생성하여 반환
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 비밀번호를 암호화해서 저장하는 인코더를 등록
        // BCrypt는 단방향 해시 함수로, 복호화는 불가능
        // 로그인할 때 입력한 비밀번호를 같은 방식으로 암호화해서 비교함
        return new BCryptPasswordEncoder();
    }
}
