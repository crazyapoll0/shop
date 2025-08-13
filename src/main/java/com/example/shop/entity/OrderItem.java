package com.example.shop.entity;

import jakarta.persistence.*;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

@Entity
@Getter @Setter
public class OrderItem extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")         // 주문상품 PK
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id")           // 주문한 상품
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")          // 이 주문상품이 속한 주문
    private Order order;

    private Integer orderPrice;             // 주문 당시 상품 가격

    private Integer count;                  // 주문 수량

//    private LocalDateTime regTime;          // 생성일

//    private LocalDateTime updateTime;       // 수정일
}