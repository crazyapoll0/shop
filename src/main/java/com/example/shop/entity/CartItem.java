package com.example.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cart_item")      // 테이블명 지정
@Getter @Setter

public class CartItem extends BaseEntity {
    @Id
    @Column(name = "cart_item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)         // 장바구니 항목 PK
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")           // 이 항목이 속한 장바구니
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")           // 장바구니에 담긴 상품
    private Item item;

    private Integer count;              // 상품 수량
}
