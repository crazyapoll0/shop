package com.example.shop.entity;

import com.shop.dto.MemberFormDto;
import com.shop.entity.Cart;
import com.shop.entity.Member;
import com.shop.repository.CartRepository;
import com.shop.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class CartTest {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    private CartRepository cartRepository;

    @PersistenceContext
    EntityManager em;

    public Member createMember() {
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail("test@test");
        memberFormDto.setName("test");
        memberFormDto.setAddress("창원시");
        memberFormDto.setPassword("123456");
        return Member.createMember(memberFormDto, passwordEncoder);
    }

    @Test
    @DisplayName("장바구니 회원 엔티티 매핑 조회 테스트")
    public void findCartAndMemberTest() {
        /*
        1. 신규 회원 등록
        2. 해당 회원 엔티티의 장바구니 생성 및 등록
        3. 저장된 장바구니를 통해 해당 회원 조회
        4. 조회된 회원과 저장한 회원 정보가 일치하는지 확인
        */
        Member member = createMember();
        memberRepository.save(member);

        Cart cart = new Cart();
        cart.setMember(member);
        cartRepository.save(cart);

        // flush/clear 전 객체의 메모리 참조값 출력
        System.out.println("저장하기 전 cart의 주소값:" + cart.hashCode());
        System.out.println("저장하기 전 member의 주소값:" + member.hashCode());

        em.flush();             // 영속성 컨텍스트의 변경사항을 DB에 반영
        em.clear();             // 영속성 컨텍스트 초기화 (모든 엔티티 준영속화)

        Optional<Cart> savedCartOp = cartRepository.findById(cart.getId());
        //옵셔널 안에 값이 존재하면 꺼내서 받고, null이면 EntityNotFoundException 던지기
        Cart savedCart = savedCartOp.orElseThrow(EntityNotFoundException::new);

        /*DB 등록
        1. 카트를 조회한다.
        2. 조회된 카트 레코드에서 member_id FK값을 얻는다.
        3. 얻은 FK를 가지고 member 테이블에서 연관된 유저를 찾는다
        OR
        4. cart 테이블과 member 테이블 사이에 member_id FK를 조건으로 JOIN한다.
        */

        //객체(JPA) 관점
        Member foundMember = savedCart.getMember();

        System.out.println("저장하고난 후 cart의 주소값:" + savedCart.hashCode());
        System.out.println("저장하고난 후 member의 주소값:" + savedCart.hashCode());
        assertEquals(foundMember.getId(), member.getId());
    }
}