package com.shop.entity;

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

    public static CartItem createCarteItem(Cart cart, Item item, Integer count) {
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setCount(count);
        return cartItem;
    }

    public void addCount(Integer count) {
        this.count  += count;
    }

    public void updateCount(Integer integer) {
        this.count = count;
    }

}


