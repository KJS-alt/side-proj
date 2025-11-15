package com.onbid.service.goods;

import com.onbid.domain.goods.dto.Goods;
import com.onbid.service.external.OnbidApiService;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 온비드 데이터 자동 동기화 스케줄러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GoodsSyncScheduler {

    private final OnbidApiService onbidApiService;
    private final GoodsService goodsService;
    private final GoodsSyncStatusService goodsSyncStatusService;

    /**
     * 매 분 100개까지 최신 데이터를 저장
     */
    @Scheduled(cron = "0 * * * * *")
    public void syncLatestGoods() {
        try {
            log.info("[Scheduler] 온비드 최신 데이터 동기화 시작");
            List<Goods> apiItems = onbidApiService.getGoodsItems(1, 9999, null, null);
            List<Goods> latest = selectTop100(apiItems);
            int synced = goodsService.saveGoodsListToDB(latest);
            goodsSyncStatusService.markSynced(java.time.LocalDateTime.now());
            log.info("[Scheduler] 온비드 동기화 완료 - {}개 반영", synced);
        } catch (Exception e) {
            log.error("[Scheduler] 온비드 동기화 실패", e);
        }
    }

    private List<Goods> selectTop100(List<Goods> items) {
        if (items == null || items.isEmpty()) {
            return List.of();
        }

        Map<String, Goods> latestByGoodsNo = new LinkedHashMap<>();
        for (Goods item : items) {
            String key = item.getGoodsNo() != null
                    ? item.getGoodsNo()
                    : String.valueOf(item.getHistoryNo());

            Goods existing = latestByGoodsNo.get(key);
            if (existing == null || isNewer(item, existing)) {
                latestByGoodsNo.put(key, item);
            }
        }

        return latestByGoodsNo.values()
                .stream()
                .filter(Objects::nonNull)
                .limit(100)
                .collect(Collectors.toList());
    }

    private boolean isNewer(Goods candidate, Goods existing) {
        long candidateHistory = candidate.getHistoryNo() == null ? 0L : candidate.getHistoryNo();
        long existingHistory = existing.getHistoryNo() == null ? 0L : existing.getHistoryNo();
        return candidateHistory > existingHistory;
    }
}

