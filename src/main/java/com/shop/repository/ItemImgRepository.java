package com.shop.repository;

import com.shop.entity.ItemImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemImgRepository extends JpaRepository<ItemImg, Long> {

/*
* FK란 item_id로 ItemImg를 조회
* SELECT *
* FROM item_img
* WHERE item_id = ?
* ORDER BY item_img_id ASC
*/

    List<ItemImg> findByItemIdOrderByIdAsc(Long itemID);

    ItemImg findByItemIdAndRepImgYn(Long itemId, String repImgYn);
}
