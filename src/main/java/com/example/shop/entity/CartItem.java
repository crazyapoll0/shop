package com.example.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cart_item")      // 테이블명 지정
@Getter @Setter

public class CartItem {
    @Id
    @Column(name = "cart_item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)         // 장바구니 항목 PK
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id")           // 이 항목이 속한 장바구니
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "item_id")           // 장바구니에 담긴 상품
    private Item item;

    private Integer count;              // 상품 수량
}
