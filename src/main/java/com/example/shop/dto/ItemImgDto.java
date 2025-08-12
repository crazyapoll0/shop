package com.example.shop.dto;

import com.example.shop.entity.ItemImg;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter @Setter
public class ItemImgDto {

    private Long id;            //이미지 식별자
    private String imgName;     //저장된 이미지 파일명
    private String oriImgName;      //원본 이미지 파일명
    private String imgUrl;          //이미지 접근 URL
    private String repImgYn;        //대표 이미지 여부 (Y/N)

    // ModelMapper 인스턴스 (엔티티 ↔ DTO 변환용)
    private static ModelMapper modelMapper = new ModelMapper();

    /* ItemImg 엔티티를 ItemImgDto로 변환하는 정적 메서드.
    ModelMapper를 사용하여 필드 자동 매핑.
    */
    public static ItemImgDto of(ItemImg itemImg) {
        return modelMapper.map(itemImg, ItemImgDto.class);
    }
}
