package com.shop.entity;

import com.shop.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter

public class Order extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")          // 주문 PK
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")     // 주문한 회원
    private Member member;

    /*@OneToMany
    일 대 다
     일 : Order
     다 : Order
     다 : OrderItem
    */

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, //read-only => DB 표현 불가
            orphanRemoval = true)
    List<OrderItem> orderItems = new ArrayList<>();

    private LocalDateTime orderDate;        // 주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;        // 주문상태 (e.g. ORDER, CANCEL)

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);              // 현재 주문(order)에 주문상품(orderItem) 추가
        orderItem.setOrder(this);                   // 주문상품에도 현재 주문을 연결 (양방향 연관관계 설정)
    }

    public static Order createOrder(Member member, List<OrderItem> orderItemList) {
        Order order = new Order();
        order.setMember(member);
        for (OrderItem orderItem : orderItemList) {
            order.addOrderItem(orderItem);              // 주문상품 하나씩 추가
        }
        order.setOrderStatus(OrderStatus.ORDER);        // 주문 상태를 'ORDER'로 설정
        order.setOrderDate(LocalDateTime.now());        // 주문 시간을 현재 시간으로 설정
        return order;
    }

    public int getTotalPrice(){
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();        // 각각의 주문상품 가격 합산
        }
        return totalPrice;
    }
}
