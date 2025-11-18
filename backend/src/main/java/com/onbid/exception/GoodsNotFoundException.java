package com.onbid.exception;

/**
 * 요청한 물건 데이터를 찾지 못했을 때 던지는 예외
 */
public class GoodsNotFoundException extends BusinessException {
    
    public GoodsNotFoundException(Long historyNo) {
        super(ErrorCode.GOODS_NOT_FOUND, "물건이력번호 %s 번을 찾을 수 없습니다.".formatted(historyNo));
    }
}


