package com.shop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CartDetailDto {

    private Long cartItemId;
    private String itemNm;
    private int price;
    private int count;
    private String imgUrl;

}
