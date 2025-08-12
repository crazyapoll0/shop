package com.example.shop.entity;

import com.example.shop.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter

public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")          // 주문 PK
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")     // 주문한 회원
    private Member member;

    /*@OneToMany
    일 대 다
     일 : Order
     다 : Order
     다 : OrderItem
    */

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) //read-only => DB 표현 불가
    List<OrderItem> orderItems = new ArrayList<>();

    private LocalDateTime orderDate;        // 주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;        // 주문상태 (e.g. ORDER, CANCEL)

    private LocalDateTime regTime;          // 생성일

    private LocalDateTime updateTime;       // 수정일
}
