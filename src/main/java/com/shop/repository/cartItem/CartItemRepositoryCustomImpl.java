package com.shop.repository.cartItem;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.dto.CartDetailDto;
import com.shop.entity.ItemImg;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.example.shop.entity.QCartItem.cartItem;
import static com.example.shop.entity.QItem.item;
import static com.example.shop.entity.QItemImg.itemImg;

@RequiredArgsConstructor
public class CartItemRepositoryCustomImpl implements CartItemRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<CartDetailDto> findCartDetailDtoList(Long cartId) {

         /*
    SELECT ci.id, i.itemNm, i.price, ci.count, im.imgUrl
    FROM cart_item ci
    INNER JOIN item i
       ON ci.item_id = i.item_id
    INNER JOIN
       ON i.item_id = in.item_id
    WHERE ci.cart_id = ?
       AND im.rep_img_Yn = 'Y'
     ORDER BY ci.regTime DESC
    */

        return jpaQueryFactory.select(Projections.fields(CartDetailDto.class,
                cartItem.id.as("cartItemId"),
                item.itemNm,
                item.price,
                cartItem.count,
                itemImg.imgUrl))
                .from(item)
                .join(cartItem)
                .on(cartItem.item.eq(item))
                .join(itemImg)
                .on(itemImg.item.eq(item))
                .where(cartItem.cart.id.eq(cartId))
                .where(itemImg.repImgYn.eq("Y"))
                .orderBy(cartItem.regTime.desc())
                .fetch();
    }
}
