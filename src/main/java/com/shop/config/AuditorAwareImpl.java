package com.shop.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

//Spring Security 기반의 AuditorAware구현
//JPA Auditing 기능을 통해 엔티티 생성자/수정자(createdBy, modifiedBy)를 자동으로 설정함.
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        /*
         * AuditorAware ==> 엔티티 생성 및 수정 시에 해당 행위의 주체(유저)의 정보를 알아내는 역활
         * 구현 : Security Context -> Authentication -> 유저 정보 -> 유저 아이디(이름) ==> 반환
         * */
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 정보가 존재할 경우 사용자 이름 반환, 없으면 빈 문자열 반환
        String userId = "";
        if(authentication != null) {
            userId = authentication.getName();
        }
        return Optional.of(userId);
    }
}
