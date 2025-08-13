package com.example.shop.controller;

import com.example.shop.dto.ItemFormDto;
import com.example.shop.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.Binding;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {

    //상품 등록 서비스
    public final ItemService itemService;

    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model) {
        //빈 DTO 객체를 모델에 담아 뷰에서 사용
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "item/itemForm";
    }

    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid @ModelAttribute ItemFormDto itemFormDto,
                          BindingResult bindingResult, Model model, @RequestParam("itemImgFile")
                          List<MultipartFile> itemImgFileList) {
        // 유효성 검사 실패 시 폼 페이지로 이동
        if (bindingResult.hasErrors()) {
            return "item/itemForm";
        }
        if (itemImgFileList.get(0).isEmpty()) {
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값입니다.");
            return "item/itemform";
        }
        try {                        // 상품 및 이미지 저장
            itemService.saveItem(itemFormDto, itemImgFileList);
        } catch (Exception e) {                     // 예외 발생 시 에러 메시지 전달 후 폼 페이지로 이동
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }
        return "redirect:/";
    }

    @GetMapping(value = "/admin/item/{itemId}")
    public String itemDtl(@PathVariable Long itemId, Model model) {

        try {               //상품 상세 정보 조회
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("itemFormDto", itemFormDto);
        } catch (Exception e) {                         //에러 메시지 전달 후 폼 페이지로 이동
            model.addAttribute("errorMessage", "상품 수정 중 에러 발생하였습니다.");
            return "item/itemForm";
        }
        //수정 폼 페이지로 이동
        return "item/itemForm";
    }

    @PostMapping("/admin/item/{itemId}")
    public String itemUpdate(@Valid @ModelAttribute ItemFormDto itemFormDto, BindingResult bindingResult,
                             Model model, @RequestParam("itemImgFile")
                          List<MultipartFile> itemImgFileList) {
        if (bindingResult.hasErrors()) {
            return "item/itemForm";
        }
        if (itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null) {
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값입니다.");
            return "item/itemForm";
        } try {                        // 상품 및 이미지 저장
            itemService.updateItem(itemFormDto, itemImgFileList);
        } catch (Exception e) {                     // 예외 발생 시 에러 메시지 전달 후 폼 페이지로 이동
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }
        return "redirect:/";
    }
}

