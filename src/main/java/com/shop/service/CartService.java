package com.shop.service;

import com.shop.dto.*;
import com.shop.entity.Cart;
import com.shop.entity.CartItem;
import com.shop.entity.Item;
import com.shop.entity.Member;
import com.shop.repository.cartItem.CartItemRepository;
import com.shop.repository.CartRepository;
import com.shop.repository.Item.ItemRepository;
import com.shop.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderService orderService;

    public Long addCart(CartItemDto cartItemDto, String email){
        /*
        1. Item 엔티티 조회
        2. Member 엔티티 조회
        3. Cart 엔티티 조회 ==> Cart 저장
        4. CartItem ==> 저장
        4-1 없다 ==> CartItem 엔티티 저장
        4-2 있다 ==> 조회된 CartItem 엔티티 count 증가
        */
        Item item = itemRepository.findById(cartItemDto.getItemId()).orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email);
        Cart cart = cartRepository.findByMember(member);
        if (cart == null) {
            cart = Cart.createcart(member);
            cartRepository.save(cart);
        }
        CartItem savedCartItem = cartItemRepository.findByCartAndItem(cart, item);
        if (savedCartItem != null) {
            savedCartItem.addCount(cartItemDto.getCount());
            return savedCartItem.getId();
        } else {
            CartItem newCartItem = CartItem.createCarteItem(cart, item, cartItemDto.getCount());
            cartItemRepository.save(newCartItem);
            return newCartItem.getId();
        }
    }
    public List<CartDetailDto> getCartList(String email) {
        /*
        Member 조회
        Cart 조회
        CartItem을 조회
        */
        Member member = memberRepository.findByEmail(email);
        Cart cart = cartRepository.findByMember(member);

        if (cart == null) {
            return new ArrayList<>();
        }
        return cartItemRepository.findCartDetailDtoList(cart.getId());
    }

    public void updateCartItemCount(Long cartItemId, int count) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                    .orElseThrow(EntityNotFoundException::new);
        cartItem.updateCount(count);
    }

    public void deleteCartItem(Long cartItemId) {
        //#1
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);
        cartItemRepository.delete(cartItem);
        //#2
        //cartItemRepository.deleteById(cartItemId);

    }

    public Long orderCartItems(List<CartOrderDto> cartOrderDtoList, String email) {
        //책임1. CartItemDto에 있는 cartItemId를 이용 ==> OrderDtoList를 생성
        List<OrderDto> orderDtoList = new ArrayList<>();
        for (CartOrderDto cartOrderDto : cartOrderDtoList) {
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId()).orElseThrow(EntityNotFoundException::new);

            OrderDto orderDto = OrderDto.builder()
                    .itemId(cartItem.getItem().getId())
                    .count(cartItem.getCount())
                    .build();
            orderDtoList.add(orderDto);
        }
        // 주문 ==> 수령 ==> orderService ==> OrderDtoList, email
        Long orderId = orderService.orders(orderDtoList, email);
        //책임2. 주문이 완료된 CartItem을 테이블에서 삭제
        for (CartOrderDto cartOrderDto : cartOrderDtoList) {
            cartItemRepository.deleteById(cartOrderDto.getCartItemId());
    }
        return orderId;
    }
}






