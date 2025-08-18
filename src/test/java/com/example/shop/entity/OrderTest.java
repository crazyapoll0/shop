package com.example.shop.entity;

import com.example.shop.constant.ItemSellStatus;
import com.example.shop.repository.Item.ItemRepository;
import com.example.shop.repository.MemberRepository;
import com.example.shop.repository.OrderItemRepository;
import com.example.shop.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class OrderTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    // 테스트용 상품 생성
    public Item createItem() {
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("상세설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        return item;
    }

    // 영속성 전이 테스트: Order 저장 시 OrderItem도 함께 저장되는지 확인
    @Test
    @DisplayName("영속성 전이 테스트")
    public void casecadeTest() {
        //1. 주문 엔티티 생성
        Order order = new Order();
        //for문의 역활
        //아이템 기준정보 생성, 그 기준정보로 주문-아이템 생성
        for (int i = 0; i < 3; i++) {
            //2. 아이템 엔티티 저장
            Item item = this.createItem();
            itemRepository.save(item);
            //3. 뒤에서 저장된 아이템으로 주문-아이템(주문과 특정 아이템을 연결해주는 역활) 저장
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1_000);
            //orderRepository.save(orderItem);
            /* 현재 Order는 저장이 안됨, order 엔티티의 orderItems 리스트에 orderItem 엔티티를 추가 */
            order.getOrderItems().add(orderItem);
        }

        /* 지금 Order는 저장 전에 OrderItem 엔티티를 3개 포함한 상태 */
        orderRepository.saveAndFlush(order);
        em.clear();

        Order savedOrder = orderRepository.findById(order.getId())
                .orElseThrow(EntityNotFoundException::new);
        assertEquals(3, savedOrder.getOrderItems().size());

    }

    //테스트용 주문 생성
    public Order createOrder() {
        Order order = new Order();
        for (int i = 0; i < 3; i++) {
            Item item = this.createItem();
            itemRepository.save(item);
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(10000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
        }

        Member member = new Member();
        memberRepository.save(member);

        order.setMember(member);
        orderRepository.save(order);
        return order;
    }

    //고아 객체 제거 테스트 : OrderItem 제거 시 DB에서도 삭제되는지 확인
    @Test
    @DisplayName("고아객체 제거 테스트")
    public void orphanRemovalTest() {
        Order order = this.createOrder();
        order.getOrderItems().remove(0);
        em.flush();
    }

    //지연 로딩 테스트: 프록시 객체 확인 및 실제 데이터 접근 시점 확인
    @Test
    @DisplayName("지연 로딩 테스트")
    public void lazyLoadingTest() {
    Order order = this.createOrder();
    Long orderItemId = order.getOrderItems().get(0).getId();
    em.flush();
    em.clear();

    OrderItem orderItem = orderItemRepository.findById(orderItemId)
            .orElseThrow(EntityNotFoundException::new);

    // 프록시 클래스 확인
    System.out.println("Order class : " + orderItem.getOrder().getClass());

    // 실제 데이터 접근 → 프록시 초기화
    System.out.println("==========================");
    orderItem.getOrder().getOrderDate();
    System.out.println("==========================");

    // 필요 시 아이템 정보 접근
    //orderItem.getItem().getItemDetail();
    }
}

