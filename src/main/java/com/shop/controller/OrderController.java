package com.shop.controller;

import com.shop.dto.OrderDto;
import com.shop.dto.OrderHistDto;
import com.shop.exception.OutOfStockException;
import com.shop.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;


@Controller
@RequiredArgsConstructor
public class OrderController {

    // 주문 비즈니스 로직을 담당하는 서비스
    private final OrderService orderService;

    @PostMapping("/order")
    @ResponseBody
    public ResponseEntity<?> order(@RequestBody @Valid OrderDto orderDto,               // 요청 본문에서 주문 정보 매핑 + 유효성 검사
                                   BindingResult bindingResult,                    // 유효성 검사 결과 저장
                                   Authentication authentication) {                        // 로그인한 사용자 정보 받기
        if (bindingResult.hasErrors()) {                //1.유효성 검사 실패 시
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            StringBuilder sb = new StringBuilder();
            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getField())
                        .append(":")
                        .append(fieldError.getDefaultMessage())
                        .append("\n");
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }
        Long orderId = null;
        try {               // 2. 주문 처리 시도 (로그인한 유저의 이름 = 이메일)
            orderId = orderService.order(orderDto, authentication.getName());
        } catch (OutOfStockException e) {                   // 3. 재고 부족 시 예외 처리
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        // 4. 정상적으로 주문 완료된 경우, 주문 ID 반환
        return ResponseEntity.ok().body(orderId);
    }

    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page")Optional<Integer> page, Principal principal, Model model) {
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 4);

        Page<OrderHistDto> orderHistDtoList = orderService.getOrderList(principal.getName(), pageable);
        model.addAttribute("orders", orderHistDtoList);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 5);
        return "order/orderHist";
    }
}
