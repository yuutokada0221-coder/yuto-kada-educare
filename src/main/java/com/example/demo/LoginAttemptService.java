package com.example.demo;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// ★ブルートフォース対策：同じユーザー名への短時間の連続ログイン失敗を検知し、一時的にロックする。
// メモリ上のカウンタのみ（単一インスタンス前提）。ロック中はパスワードが合っていても弾く。
@Component
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration WINDOW = Duration.ofMinutes(5);
    private static final Duration LOCK_DURATION = Duration.ofMinutes(5);

    private static class Attempt {
        int count;
        Instant windowStart;
        Instant lockedUntil;
    }

    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();

    public void recordFailure(String username) {
        if (username == null || username.isBlank()) return;
        String key = username.trim().toLowerCase();
        Attempt a = attempts.computeIfAbsent(key, k -> new Attempt());
        synchronized (a) {
            Instant now = Instant.now();
            if (a.windowStart == null || Duration.between(a.windowStart, now).compareTo(WINDOW) > 0) {
                a.windowStart = now;
                a.count = 0;
            }
            a.count++;
            if (a.count >= MAX_ATTEMPTS) {
                a.lockedUntil = now.plus(LOCK_DURATION);
            }
        }
    }

    public void recordSuccess(String username) {
        if (username == null) return;
        attempts.remove(username.trim().toLowerCase());
    }

    public boolean isLocked(String username) {
        if (username == null || username.isBlank()) return false;
        Attempt a = attempts.get(username.trim().toLowerCase());
        if (a == null || a.lockedUntil == null) return false;
        if (Instant.now().isAfter(a.lockedUntil)) {
            attempts.remove(username.trim().toLowerCase());
            return false;
        }
        return true;
    }

    public long secondsRemaining(String username) {
        if (username == null) return 0;
        Attempt a = attempts.get(username.trim().toLowerCase());
        if (a == null || a.lockedUntil == null) return 0;
        return Math.max(0, Duration.between(Instant.now(), a.lockedUntil).getSeconds());
    }
}
