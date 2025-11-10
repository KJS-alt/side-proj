package com.onbid.controller;

import com.onbid.service.OnbidApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Slf4j
@Controller
@RequestMapping("/goods")
@RequiredArgsConstructor
public class GoodsController {

    private final OnbidApiService onbidApiService;

    @GetMapping("/list")
    public String getGoodsList(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int numOfRows,
            @RequestParam(required = false) String ctgrHirkId,  // 카테고리 추가
            @RequestParam(required = false) String sido,
            @RequestParam(required = false) String sgk,
            @RequestParam(required = false) String emd,
            @RequestParam(required = false) String goodsPriceFrom,
            @RequestParam(required = false) String goodsPriceTo,
            @RequestParam(required = false) String openPriceFrom,
            @RequestParam(required = false) String openPriceTo,
            @RequestParam(required = false) String cltrNm,
            @RequestParam(required = false) String pbctBegnDtm,
            @RequestParam(required = false) String pbctClsDtm,
            @RequestParam(required = false) String cltrMnmtNo,
            Model model) {

        log.info("물건 목록 조회 - 페이지: {}, 개수: {}, 카테고리: {}", pageNo, numOfRows, ctgrHirkId);

        try {
            // API 호출
            String xmlResponse = onbidApiService.getGoodsList(
                    pageNo, numOfRows, ctgrHirkId, sido, sgk, emd,
                    goodsPriceFrom, goodsPriceTo,
                    openPriceFrom, openPriceTo,
                    cltrNm, pbctBegnDtm, pbctClsDtm, cltrMnmtNo
            );

            // 모델에 데이터 추가
            model.addAttribute("success", true);
            model.addAttribute("xmlResponse", xmlResponse);
            model.addAttribute("pageNo", pageNo);
            model.addAttribute("numOfRows", numOfRows);

            // 검색 조건들도 모델에 추가 (폼 유지용)
            model.addAttribute("ctgrHirkId", ctgrHirkId);  // 카테고리 추가
            model.addAttribute("sido", sido);
            model.addAttribute("sgk", sgk);
            model.addAttribute("emd", emd);
            model.addAttribute("goodsPriceFrom", goodsPriceFrom);
            model.addAttribute("goodsPriceTo", goodsPriceTo);
            model.addAttribute("openPriceFrom", openPriceFrom);
            model.addAttribute("openPriceTo", openPriceTo);
            model.addAttribute("cltrNm", cltrNm);
            model.addAttribute("pbctBegnDtm", pbctBegnDtm);
            model.addAttribute("pbctClsDtm", pbctClsDtm);
            model.addAttribute("cltrMnmtNo", cltrMnmtNo);

        } catch (Exception e) {
            log.error("물건 목록 조회 실패", e);
            model.addAttribute("success", false);
            model.addAttribute("error", e.getMessage());
        }

        return "goods-list";
    }

    /**
     * 메인 페이지
     */
    @GetMapping("/")
    public String main() {
        return "main";
    }

    /**
     * 검색 페이지
     */
    @GetMapping("/search")
    public String search(
            @RequestParam(required = false) String ctgrHirkId,  // 카테고리 추가
            @RequestParam(required = false) String sido,
            @RequestParam(required = false) String sgk,
            @RequestParam(required = false) String emd,
            @RequestParam(required = false) String goodsPriceFrom,
            @RequestParam(required = false) String goodsPriceTo,
            @RequestParam(required = false) String openPriceFrom,
            @RequestParam(required = false) String openPriceTo,
            @RequestParam(required = false) String cltrNm,
            @RequestParam(required = false) String pbctBegnDtm,
            @RequestParam(required = false) String pbctClsDtm,
            @RequestParam(required = false) String cltrMnmtNo,
            @RequestParam(defaultValue = "10") int numOfRows,
            @RequestParam(defaultValue = "1") int pageNo,
            Model model) {

        // 검색 조건을 모델에 추가 (폼 값 유지용)
        model.addAttribute("ctgrHirkId", ctgrHirkId);  // 카테고리 추가
        model.addAttribute("sido", sido);
        model.addAttribute("sgk", sgk);
        model.addAttribute("emd", emd);
        model.addAttribute("goodsPriceFrom", goodsPriceFrom);
        model.addAttribute("goodsPriceTo", goodsPriceTo);
        model.addAttribute("openPriceFrom", openPriceFrom);
        model.addAttribute("openPriceTo", openPriceTo);
        model.addAttribute("cltrNm", cltrNm);
        model.addAttribute("pbctBegnDtm", pbctBegnDtm);
        model.addAttribute("pbctClsDtm", pbctClsDtm);
        model.addAttribute("cltrMnmtNo", cltrMnmtNo);
        model.addAttribute("numOfRows", numOfRows);
        model.addAttribute("pageNo", pageNo);

        return "goods-search";
    }

    /**
     * 홈 페이지 (메인으로 리다이렉트)
     */
    @GetMapping
    public String home() {
        return "redirect:/goods/";
    }
}