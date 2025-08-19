package com.shop.repository.order;

import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.example.shop.entity.QOrder.order;

@RequiredArgsConstructor

public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {
private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Order> findOrders(String email, Pageable pageable) {
        /*
        SELECT *
        FROM orders o
        INNER JOIN member o
        ON o.member_id = m.member_id
        WHERE o.email = ?
        ORDER BY o.order_date DESC
        */

        return jpaQueryFactory
        .select(order)
                .from(order)
                .innerJoin(order.member)
                .where(order.member.email.eq(email))
                .orderBy(order.orderDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

    }

    @Override
    public Long countOrder(String email) {
        /*
        SELECT count(*)
        FROM orders
        INNER JOIN member m
        ON o.member_id = m.member_id
        WHERE m.email = ?
        */

        Long total = jpaQueryFactory
                .select(Wildcard.count)
                .from(order)
                .innerJoin(order.member)
                .where(order.member.email.eq(email))
                .fetchOne();

        Optional<Long> totalCount = Optional.ofNullable(total);

        return totalCount.orElse(0L);
    }
}
