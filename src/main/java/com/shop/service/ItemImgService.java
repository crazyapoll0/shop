package com.shop.service;

import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor

public class ItemImgService {

    @Value("${itemImgLocation}")
    private String itemImgLocation;

    private final ItemImgRepository itemImgRepository;

    private final FileService fileService;

    /*
    * 클라이언트(브라우저)가 보낸 item 이미지 정보, 첨부파일
    ==> 요청 바디에서 데이터를 자바 객체로 바인딩 하는 책임 ==> 컨트롤러 서비스 책임 }
    1. MulpartFile에 들어있는 이미지 파일 ==> 파일시스템에 저장 ==> FileService 호출
    2. DB에 저장할 ItemImg 정보 ==> DB에 저장 ==> ItemImgRepository
    */
    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception {
        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        //파일 시스템에 파일 업로드 . . . 1
        if (!StringUtils.isEmpty(oriImgName)) {
            imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());

            imgUrl = "/images/item/" + imgName;
        }
        itemImg.updateItemImg(oriImgName, imgName, imgUrl);

        //DB에 저장된 파일 정보 저장 . . . 2
        itemImgRepository. save(itemImg);
    }

    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception {
        if(!itemImgFile.isEmpty()) {
            ItemImg savedItemImg = itemImgRepository.findById(itemImgId).orElseThrow(EntityNotFoundException::new);

            //1. 기존에 파일시스템에 저장되어있던 이미지 삭제
            if (!StringUtils.isEmpty(savedItemImg.getImgName())) {
                fileService.deletedFile(itemImgLocation+"/"+savedItemImg.getImgName());
            }
            //2. DB에 기존 레코드 내용 업데이트(새로 저장된 이미지의 경로, 이름, 원본 이미지 이름)
            //JPA Entity Update ==> dirty checking ==> 조회된 엔티티의 필드 변경
            String oriImgName = itemImgFile.getOriginalFilename();
            //새 파일 저장하고 저장된 파일명 받기
            String imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            String imgUrl = "/images/item/" + imgName;
            savedItemImg.updateItemImg(oriImgName, imgName, imgUrl);
        }
    }


}
