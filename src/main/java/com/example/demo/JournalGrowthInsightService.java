package com.example.demo;

import com.example.demo.entity.DailyJournal;
import com.example.demo.entity.JournalGrowthInsight;
import com.example.demo.entity.UserAccount;
import com.example.demo.repository.DailyJournalRepository;
import com.example.demo.repository.JournalGrowthInsightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

// ★夜のジャーナルタブ：直近30日分の記録をOpenAI APIに渡し、前向きな成長分析コメントを生成する。
// APIキー未設定や呼び出し失敗でもホーム画面自体は必ず表示できるよう、例外は全てここで吸収する
// （EmailService/PushNotificationServiceと同じ「外部連携は失敗してもアプリを止めない」方針）。
@Service
public class JournalGrowthInsightService {

    private static final Logger log = LoggerFactory.getLogger(JournalGrowthInsightService.class);

    private static final int LOOKBACK_DAYS = 30;
    // ★初めて夜のジャーナルを1件書いた時点から全ユーザーが使える機能にするため1件以上で生成する
    // （以前は3件必須にしていたため、記録が少ない一般ユーザーには「使えない機能」に見えてしまっていた）
    private static final int MIN_ENTRIES = 1;

    public enum Status { READY, NOT_ENOUGH_DATA, NOT_CONFIGURED, GENERATION_FAILED }

    public record GrowthInsightView(Status status, String message, int entryCount, int minEntries) {
        static GrowthInsightView ready(String message, int entryCount) {
            return new GrowthInsightView(Status.READY, message, entryCount, MIN_ENTRIES);
        }
        static GrowthInsightView notEnoughData(int entryCount) {
            return new GrowthInsightView(Status.NOT_ENOUGH_DATA, null, entryCount, MIN_ENTRIES);
        }
        static GrowthInsightView notConfigured(int entryCount) {
            return new GrowthInsightView(Status.NOT_CONFIGURED, null, entryCount, MIN_ENTRIES);
        }
        static GrowthInsightView generationFailed(int entryCount) {
            return new GrowthInsightView(Status.GENERATION_FAILED, null, entryCount, MIN_ENTRIES);
        }
    }

    @Autowired private DailyJournalRepository journalRepository;
    @Autowired private JournalGrowthInsightRepository insightRepository;

    @Value("${openai.api.key:}")
    private String apiKey;

    @Value("${openai.api.model:gpt-4o-mini}")
    private String model;

    private final RestClient restClient;

    // ★baseUrlを設定可能にしているのは、Azure OpenAI等の互換エンドポイントに向け先を変えられるようにするため
    public JournalGrowthInsightService(@Value("${openai.api.base-url:https://api.openai.com/v1}") String baseUrl) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5_000);
        requestFactory.setReadTimeout(20_000);
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    @Transactional
    public GrowthInsightView getOrGenerate(UserAccount user, LocalDate today) {
        List<DailyJournal> entries = journalRepository.findByUserAccountAndDateBetween(
                user, today.minusDays(LOOKBACK_DAYS - 1), today);

        if (entries.size() < MIN_ENTRIES) {
            return GrowthInsightView.notEnoughData(entries.size());
        }

        Optional<JournalGrowthInsight> cached = insightRepository.findByUserAccount(user);
        if (cached.isPresent() && today.equals(cached.get().getGeneratedDate())) {
            return GrowthInsightView.ready(cached.get().getContent(), entries.size());
        }

        if (apiKey == null || apiKey.isBlank()) {
            // 未設定でも、以前生成済みのキャッシュがあればそれを見せ続ける（機能が急に消えたように見えないように）
            return cached.map(c -> GrowthInsightView.ready(c.getContent(), entries.size()))
                    .orElseGet(() -> GrowthInsightView.notConfigured(entries.size()));
        }

        try {
            String generated = callOpenAi(user, entries);
            JournalGrowthInsight insight = cached.orElseGet(JournalGrowthInsight::new);
            insight.setUserAccount(user);
            insight.setGeneratedDate(today);
            insight.setContent(generated);
            insightRepository.save(insight);
            return GrowthInsightView.ready(generated, entries.size());
        } catch (Exception e) {
            log.warn("ジャーナル成長分析の生成に失敗しました（ユーザー: {}）: {}", user.getUsername(), e.getMessage());
            return cached.map(c -> GrowthInsightView.ready(c.getContent(), entries.size()))
                    .orElseGet(() -> GrowthInsightView.generationFailed(entries.size()));
        }
    }

    private String callOpenAi(UserAccount user, List<DailyJournal> entries) {
        String journalDigest = entries.stream()
                .sorted(Comparator.comparing(DailyJournal::getDate))
                .map(e -> "・%s（気分%d/5）達成:%s / 感謝:%s,%s,%s / 日記:%s".formatted(
                        e.getDate(), e.getMoodScore(),
                        blankToDash(e.getAchievement()), blankToDash(e.getGratitude1()),
                        blankToDash(e.getGratitude2()), blankToDash(e.getGratitude3()),
                        blankToDash(e.getDiaryText())))
                .collect(Collectors.joining("\n"));

        String systemPrompt = "あなたは、ユーザーの一番の味方であるパーソナルコーチです。ユーザーの直近30日分のジャーナル記録を読み、"
                + "その人にしか言えない具体的な成長ポイントを見つけて、まるで親しい友人がメッセージを送るように、直接語りかけてください。\n\n"
                + "厳守事項：\n"
                + "・「〜と書いてありますね」「〜だと思います」「〜のようです」のような観察・レポート口調は絶対に使わないこと。\n"
                + "・必ず「〜さん」と名前を呼びかけ、驚きや喜びを込めた「〜じゃないですか！」「〜なんてすごいですね！」のような、テンションの高い話し言葉で書くこと。\n"
                + "・感嘆符（！）を積極的に使い、抑揚のない棒読みの分析にしないこと。\n"
                + "・記録の中の具体的な言葉（日記の一文や達成したことなど）を引用して触れること。\n"
                + "・最後は次の行動を後押しする一言（例：「明日はどんな1日になるか、楽しみですね！」）で締めくくること。\n\n"
                + "文体の見本（このくらいのテンションで書くこと）：\n"
                + "「○○さん、3日連続で筋トレできてるなんてすごいじゃないですか！気分もずっと良好で、まさに絶好調ですね。"
                + "『集中して勉強できた』っていう日記、読んでてこっちまで嬉しくなりました。この調子で、明日もいい1日にしていきましょう！」\n\n"
                + "記録が1〜2件と少ない場合は、無理に「成長」や「変化」を語らず、その少ない記録の中にある良い点を全力で褒めて、"
                + "続けたくなる後押しにすること。130〜200文字程度、絵文字は使わないこと。";

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", user.getUsername() + "さんの直近のジャーナル記録:\n" + journalDigest)
                ),
                "max_tokens", 300,
                "temperature", 0.9
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        if (response == null) {
            throw new IllegalStateException("OpenAI APIから空の応答が返されました");
        }
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        if (choices == null || choices.isEmpty()) {
            throw new IllegalStateException("OpenAI APIの応答にchoicesが含まれていません");
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        String content = message == null ? null : (String) message.get("content");
        if (content == null || content.isBlank()) {
            throw new IllegalStateException("OpenAI APIの応答に本文が含まれていません");
        }
        return content.trim();
    }

    private static String blankToDash(String value) {
        return (value == null || value.isBlank()) ? "-" : value;
    }
}
