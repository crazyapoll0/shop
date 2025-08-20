package com.shop.service;

import com.shop.dto.OrderDto;
import com.shop.dto.OrderHistDto;
import com.shop.dto.OrderItemDto;
import com.shop.entity.*;
import com.shop.repository.Item.ItemRepository;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.order.OrderRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemImgRepository itemImgRepository;

    public Long orders(List<OrderDto> orderDtoList, String email) {
        Member member = memberRepository.findByEmail(email);
        List<OrderItem> orderItemList = new ArrayList<>();
        for (OrderDto orderDto : orderDtoList) {
            Item item = itemRepository.findById(orderDto.getItemId()).orElseThrow(EntityNotFoundException::new);

            OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
            orderItemList.add(orderItem);
        }

        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);
        return order.getId();
    }

    public Long order(OrderDto orderDto, String email) {
        /*
        1. orderDto.itemId ==> 아이템 엔티티 조회
        2. email ==> 유저 엔티티 조회
        3. 오더 아이템 엔티티 생성
        4. 오더 인티티 생성 ==> 오더 엔티티.orderItems(List)에 오더 아이템 엔티티 추가
        5. 오더 엔티티 저장 ==> 오더 아이템 엔티티 저장
        */

      Item item = itemRepository.findById(orderDto.getItemId()).orElseThrow(EntityExistsException::new);
      Member member = memberRepository.findByEmail(email);

      List<OrderItem> orderItemList = new ArrayList<>();
      OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
      orderItemList.add(orderItem);

      Order order = Order.createOrder(member, orderItemList);

      orderRepository.save(order);
      return order.getId();
    }

    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable) {

        List<Order> orders = orderRepository.findOrders(email, pageable);
        Long totalCount = orderRepository.countOrder(email);

        List<OrderHistDto> orderHistDtos = new ArrayList<>();

        for (Order order : orders) {
            OrderHistDto orderHistDto = new OrderHistDto(order);
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepImgYn(orderItem.getItem().getId(),"Y");
                OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());
                orderHistDto.addOrderItemDto(orderItemDto);
            }
            orderHistDtos.add(orderHistDto);
        }
        return new PageImpl<OrderHistDto>(orderHistDtos, pageable, totalCount);
    }

    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(EntityExistsException::new);
        order.cancelOrder();
    }

    public boolean validateOrder(Long orderId, String email) {
        //orderId로 조회된 Order 엔티티의 member.email == email ?
        Order order = orderRepository.findById(orderId)
                                            .orElseThrow(EntityNotFoundException::new);
        return StringUtils.equals(email, order.getMember().getEmail());
    }
}















