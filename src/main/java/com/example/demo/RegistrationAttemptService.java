package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// ★同一IPからの大量アカウント登録（スパム／なりすまし用アカウントの量産）を防ぐ簡易レートリミッタ。
// ログイン試行と違い「失敗」ではなく「成功した登録そのもの」の回数を数える。
// 上限は設定で変えられるようにしておく（MockMvcの統合テストは全部同じ127.0.0.1から何十件も登録するため、
// テスト用プロパティでは実質無制限に近い値を入れている）。
@Component
public class RegistrationAttemptService {

    @Value("${registration.max-per-hour:5}")
    private int maxRegistrations;

    private static final Duration WINDOW = Duration.ofHours(1);

    private static class Window {
        int count;
        Instant windowStart;
    }

    private final Map<String, Window> attempts = new ConcurrentHashMap<>();

    public boolean isRateLimited(String ip) {
        if (ip == null) return false;
        Window w = attempts.get(ip);
        if (w == null) return false;
        synchronized (w) {
            if (Duration.between(w.windowStart, Instant.now()).compareTo(WINDOW) > 0) return false;
            return w.count >= maxRegistrations;
        }
    }

    public void record(String ip) {
        if (ip == null) return;
        Window w = attempts.computeIfAbsent(ip, k -> new Window());
        synchronized (w) {
            Instant now = Instant.now();
            if (w.windowStart == null || Duration.between(w.windowStart, now).compareTo(WINDOW) > 0) {
                w.windowStart = now;
                w.count = 0;
            }
            w.count++;
        }
    }
}
