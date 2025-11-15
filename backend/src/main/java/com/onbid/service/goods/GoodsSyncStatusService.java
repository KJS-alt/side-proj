package com.onbid.service.goods;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.stereotype.Service;

/**
 * 공매 동기화 상태 저장용 서비스
 */
@Service
public class GoodsSyncStatusService {

    private final AtomicReference<LocalDateTime> lastSyncedAtRef = new AtomicReference<>();

    public void markSynced(LocalDateTime syncedAt) {
        lastSyncedAtRef.set(syncedAt);
    }

    public LocalDateTime getLastSyncedAt() {
        return lastSyncedAtRef.get();
    }

    public LocalDateTime getNextSyncAt() {
        LocalDateTime last = lastSyncedAtRef.get();
        return last != null ? last.plusMinutes(1) : null;
    }

    public long getSecondsUntilNextSync() {
        LocalDateTime next = getNextSyncAt();
        if (next == null) {
            return -1L;
        }
        long seconds = Duration.between(LocalDateTime.now(), next).getSeconds();
        return Math.max(seconds, 0);
    }
}

