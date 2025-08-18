package com.example.shop.repository.Item;

import com.example.shop.constant.ItemSellStatus;
import com.example.shop.dto.ItemSearchDto;
import com.example.shop.dto.MainItemDto;
import com.example.shop.entity.Item;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.shop.entity.QItem.item;
import static com.example.shop.entity.QItemImg.itemImg;

@RequiredArgsConstructor
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

    // JPAQueryFactory를 주입받아 Querydsl 쿼리를 생성
    private final EntityManager em;
    private final JPAQueryFactory jpaQueryFactory;

    private BooleanExpression regDtsAfter(String searchDataType) {
        // 현재 시간을 기준으로 검색 시작 시간을 계산
        LocalDateTime dateTime = LocalDateTime.now();

        // StringUtils.equals를 사용하여 NullPointerException 방지 및 문자열 비교
        LocalDateTime now = LocalDateTime.now();
        if (StringUtils.equals(searchDataType, "id")){
            now = now.minusDays(1);
        }
        else if (StringUtils.equals(searchDataType, "1w")) {
            now = now.minusWeeks(1);
        }
        else if (StringUtils.equals(searchDataType, "1m")) {
            now = now.minusMonths(1);
        }
        else if (StringUtils.equals(searchDataType, "6m")) {
            now = now.minusMonths(6);
        }
        else if (StringUtils.equals(searchDataType, "all") || searchDataType == null) {
            // 검색 기간이 "all"이거나 null인 경우, 시간 조건이 필요 없으므로 null 반환
            return null;
        }

        // item.regTime이 계산된 시간(now) 이후인 조건을 반환
        return item.regTime.after(now);
    }

    private BooleanExpression searchSellStatusEq(ItemSellStatus itemSellStatus) {
        if (itemSellStatus == null) {
            return null;
        }
        // item.itemSellStatus가 주어진 itemSellStatus와 같은 조건을 반환
        return item.itemSellStatus.eq(itemSellStatus);
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery) {
        // 무엇을 기준으로(serachBy) 검색할 키워드(serchQueary)
        // serachBy 화면 ==> "itemNm", "createdBy"
        if (StringUtils.equals(searchBy, "itemNm")) {
            return item.itemNm.like("%" + searchQuery + "%");
        }else if (StringUtils.equals(searchBy, "createdBy")) {
            return item.createdBy.like("%" + searchQuery + "%");
        }
        return null;
    }

    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        /*
        목적 : Item 테이블에서 검색 조건에 맞는 결과를 페이지 단위로 조회
        조건1. searchDataType에 따라 검색 기간 설정
        조건2. searchSellStatus에 따라 상품 판매 상태(SOLD_OUt, SELL) 설정
        조건3. searchBy + serchQueary에 따라 검색 키워드 설정
         ==> item_id를 기준으로 내림차순, pageable 기준에 따른 페이징 결과 설정

        SELECT FROM item
        WHERE 조건1 AND 조건2 AND 조건3
        OREDER BY item_id DESC
        LIMLIT, OFFSET, , ,

        Page(인터페이스)-PageImpl(구현체)
        PageImpl
        ㄴcontent : List<T>
        ㄴtotalCount : 총 페이지 수
        ㄴnumber : 페이지 번호
        */

        // Querydsl을 사용하여 동적 쿼리 생성 및 실행
        List<Item> content = jpaQueryFactory.selectFrom(item)
                .where(regDtsAfter(itemSearchDto.getSearchDataType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
                // 상품 ID를 기준으로 내림차순 정렬 (최신 상품이 먼저 오도록)
                .orderBy(item.id.desc())
                // 페이징 처리를 위해 offset과 limit 설정
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                // 쿼리 실행 및 결과 리스트 반환
                .fetch();

        /*
        SELECT count(*)
        From item
        WHERE 조건1 AND 조건2 AND 조건3
        */

       Long totalCount = jpaQueryFactory
                .select(Wildcard.count)
                .from(item)
                .where(regDtsAfter(itemSearchDto.getSearchDataType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
                .fetchOne();

        //COUNT 쿼리면 totalCount 결과는 null 될 수 있음
        //==> null을 안전하게 다루기 위해 Optional 타입으로 한번 감싼다.
        Optional<Long> total = Optional.ofNullable(totalCount);

        //total ==> Optional 타입을 이용해 Null  안전하게 처리한다.
        return new PageImpl<>(content, pageable, total.orElse(0L));
    }

    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        List<MainItemDto> content = jpaQueryFactory
                .select(Projections.fields(MainItemDto.class,
                        item.id,
                        item.itemNm,
                        item.itemDetail,
                        item.price,
                        itemImg.imgUrl
                        ))
                .from(itemImg)
                .innerJoin(itemImg.item)
                .where(itemImg.repImgYn.eq("Y"))
                .where(searchByLike("itemNm", itemSearchDto.getSearchQuery()))
                        .orderBy(item.id.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();

        Long totalCount = jpaQueryFactory
                .select(Wildcard.count)
                .from(itemImg)
                .innerJoin(itemImg.item)
                .where(itemImg.repImgYn.eq("Y"))
                .where(searchByLike("itemNm", itemSearchDto.getSearchQuery()))
                .fetchOne();

        Optional<Long> total = Optional.ofNullable(totalCount);
        return new PageImpl<>(content, pageable, total.orElse(0L));
    }
}
