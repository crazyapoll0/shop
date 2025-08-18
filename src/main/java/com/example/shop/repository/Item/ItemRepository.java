package com.example.shop.repository.Item;

import com.example.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {
    List<Item> findByItemNm(String itemNm);
    //SELECT = FROM item WHERE item_num = ?;

    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);
    // SELECT = FROM item WHERE item_num = ? item_detail= ?;

    List<Item> findByPriceLessThan(Integer price);
    //SELECT = FROM item WHERE price = ?;

    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);
    //SELECT = FROM item WHERE price < ? order by price desc;

    @Query("select i from Item i where i.itemDetail like %:itemDetail% order by i.price desc")
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);

    @Query(value="select * from item i where i.item_detail like %:itemDetail% order by i.price desc", nativeQuery = true)
    List<Item> findByItemDetailByNative(@Param("itemDetail") String itemDetail);

}
