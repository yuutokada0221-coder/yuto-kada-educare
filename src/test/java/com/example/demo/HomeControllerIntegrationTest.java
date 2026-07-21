package com.example.demo;

import com.example.demo.entity.LoginRecord;
import com.example.demo.entity.PasswordResetToken;
import com.example.demo.entity.Quest;
import com.example.demo.entity.UserAccount;
import com.example.demo.repository.FriendshipRepository;
import com.example.demo.repository.LoginRecordRepository;
import com.example.demo.repository.PasswordResetTokenRepository;
import com.example.demo.repository.QuestClaimRepository;
import com.example.demo.repository.QuestRepository;
import com.example.demo.repository.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// ★コアロジック（EXP付与・ストリーク計算・クエスト達成判定・ボス撃破判定）を、
// 実際のHTTPフロー（MockMvc）を通して検証する統合テスト。
// テスト専用のH2インメモリDB（src/test/resources/application.properties）を使うため、
// 本番のPostgres（habit_app）には一切触れない。
// @AutoConfigureMockMvcは、Spring Securityのフィルタチェーンを含む実際のフィルタ構成のまま
// MockMvcを組み立ててくれるため、spring-security-test（未導入）がなくてもCSRF等を正しく検証できる。
//
// ★MockMvcのセッションはCookieヘッダの値では引き継がれない（実サーバーと違い、
// Cookie文字列からセッションストアを引く仕組みがない）。そのため、直前のレスポンスから
// 取り出したMockHttpSessionオブジェクト自体を次のリクエストに.session(...)で明示的に渡す。
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional // 各テストの最後に自動ロールバックされ、テスト間でDB状態が持ち越されない
class HomeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserAccountRepository userRepository;

    @Autowired
    private LoginRecordRepository loginRecordRepository;

    @Autowired
    private QuestRepository questRepository;

    @Autowired
    private QuestClaimRepository questClaimRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;

    // ---------------- テスト用ヘルパー ----------------

    private static class Ctx {
        final MockHttpSession session;
        final CsrfToken csrf;
        Ctx(MockHttpSession session, CsrfToken csrf) { this.session = session; this.csrf = csrf; }
    }

    /** 指定URLをGETし、そのレスポンスに紐づくセッションとCSRFトークンをセットで取り出す */
    private Ctx fetchCtx(String url, MockHttpSession existingSession) throws Exception {
        MvcResult result = existingSession == null
                ? mockMvc.perform(get(url)).andReturn()
                : mockMvc.perform(get(url).session(existingSession)).andReturn();
        CsrfToken csrf = (CsrfToken) result.getRequest().getAttribute(CsrfToken.class.getName());
        MockHttpSession session = (MockHttpSession) result.getRequest().getSession();
        return new Ctx(session, csrf);
    }

    /** 新規ユーザーを登録し、オンボーディングを済ませた状態でログインし、以後使える認証済みセッションを返す */
    private MockHttpSession registerAndLogin(String username, String password) throws Exception {
        Ctx registerCtx = fetchCtx("/register", null);
        mockMvc.perform(post("/register")
                        .session(registerCtx.session)
                        .param("username", username)
                        .param("password", password)
                        .param(registerCtx.csrf.getParameterName(), registerCtx.csrf.getToken()))
                .andExpect(status().is3xxRedirection());

        // オンボーディングは本テストの対象外なので、直接完了扱いにしてEXPをリセットしておく
        UserAccount user = userRepository.findByUsername(username);
        user.setOnboardingCompleted(true);
        user.setExp(0);
        userRepository.save(user);

        Ctx loginCtx = fetchCtx("/login", null);
        MvcResult loginResult = mockMvc.perform(post("/login")
                        .session(loginCtx.session)
                        .param("username", username)
                        .param("password", password)
                        .param("loginType", "user")
                        .param(loginCtx.csrf.getParameterName(), loginCtx.csrf.getToken()))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        return (MockHttpSession) loginResult.getRequest().getSession();
    }

    private int expOf(String username) {
        return userRepository.findByUsername(username).getExp();
    }

    // ---------------- テスト本体 ----------------

    @Test
    void registerAndLogin_succeeds() throws Exception {
        MockHttpSession session = registerAndLogin("test_reglogin", "password123");
        mockMvc.perform(get("/").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("目標達成RPG")));
    }

    @Test
    void loginWithoutCsrfToken_isRejected() throws Exception {
        registerAndLogin("test_nocsrf", "password123");
        mockMvc.perform(post("/login")
                        .param("username", "test_nocsrf")
                        .param("password", "password123")
                        .param("loginType", "user"))
                .andExpect(status().isForbidden());
    }

    @Test
    void completingTwoOfThreeTodos_awardsTwoExpAndTwoDamage() throws Exception {
        MockHttpSession session = registerAndLogin("test_todo2", "password123");
        Ctx ctx = fetchCtx("/", session);

        mockMvc.perform(post("/saveTask")
                        .session(ctx.session)
                        .param("task1", "読書")
                        .param("task1Done", "true")
                        .param("task2", "運動")
                        .param("task2Done", "true")
                        .param("task3", "掃除")
                        .param("task3Done", "false")
                        .param(ctx.csrf.getParameterName(), ctx.csrf.getToken()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?damage=2"));

        // +1はfetchCtx()内のGET "/"で本日初回訪問のログインボーナスが入るため（+2はTODO2件分）
        assertThat(expOf("test_todo2")).isEqualTo(3);
    }

    @Test
    void completingAllThreeTodos_defeatsBoss() throws Exception {
        MockHttpSession session = registerAndLogin("test_boss_full", "password123");
        Ctx ctx = fetchCtx("/", session);

        mockMvc.perform(post("/saveTask")
                        .session(ctx.session)
                        .param("task1", "読書").param("task1Done", "true")
                        .param("task2", "運動").param("task2Done", "true")
                        .param("task3", "掃除").param("task3Done", "true")
                        .param(ctx.csrf.getParameterName(), ctx.csrf.getToken()))
                .andExpect(redirectedUrl("/?damage=3"));

        mockMvc.perform(get("/").session(session).param("damage", "3"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("モンスターを撃破した")));
    }

    @Test
    void completingTwoOfThreeTodos_doesNotDefeatBoss() throws Exception {
        MockHttpSession session = registerAndLogin("test_boss_partial", "password123");
        Ctx ctx = fetchCtx("/", session);

        mockMvc.perform(post("/saveTask")
                        .session(ctx.session)
                        .param("task1", "読書").param("task1Done", "true")
                        .param("task2", "運動").param("task2Done", "true")
                        .param("task3", "掃除").param("task3Done", "false")
                        .param(ctx.csrf.getParameterName(), ctx.csrf.getToken()))
                .andExpect(redirectedUrl("/?damage=2"));

        mockMvc.perform(get("/").session(session).param("damage", "2"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("モンスターを撃破した"))));
    }

    @Test
    void journalWithFiveFields_awardsFiveExp() throws Exception {
        MockHttpSession session = registerAndLogin("test_journal5", "password123");
        Ctx ctx = fetchCtx("/", session);

        // ★新しい経験値カーブ（Lv2境界=累計5EXP）だと、素のログインボーナスだけでも境界に近づいてしまい、
        // このテストの主旨（ジャーナル5項目で+5EXPが付与されること）と無関係にレベルアップが起きてしまう。
        // レベル境界から離れた値に揃えてから実行することで、redirectedUrl("/")の検証を安定させる。
        UserAccount user = userRepository.findByUsername("test_journal5");
        user.setExp(20); // Lv3の範囲内（累計15〜29）に十分な余裕を持たせておく
        userRepository.save(user);

        mockMvc.perform(post("/saveJournal")
                        .session(ctx.session)
                        .param("achievement", "早起きできた")
                        .param("gratitude1", "天気")
                        .param("gratitude2", "家族")
                        .param("diaryText", "良い一日だった")
                        .param("moodScore", "4")
                        .param(ctx.csrf.getParameterName(), ctx.csrf.getToken()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        assertThat(expOf("test_journal5")).isEqualTo(25);
    }

    // ★過去に発見・修正したバグの回帰テスト：写真が不正な形式でも、本文（テキスト項目）は保存され、EXPも付与される
    @Test
    void journalWithInvalidPhoto_stillSavesTextFieldsAndAwardsExp() throws Exception {
        MockHttpSession session = registerAndLogin("test_journal_badphoto", "password123");
        Ctx ctx = fetchCtx("/", session);

        MockMultipartFile fakePhoto = new MockMultipartFile("photo", "fake.png", "image/png", "not a real image".getBytes());

        mockMvc.perform(multipart("/saveJournal")
                        .file(fakePhoto)
                        .session(ctx.session)
                        .param("achievement", "本文は残るはず")
                        .param("moodScore", "5")
                        .param(ctx.csrf.getParameterName(), ctx.csrf.getToken()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?error=journalPhotoInvalid"));

        // +1はfetchCtx()内のGET "/"での本日初回ログインボーナス。
        // 写真は無効でも、本文2項目分（達成できたこと＋気分）のEXPはきちんと付与されている
        assertThat(expOf("test_journal_badphoto")).isEqualTo(3);
    }

    @Test
    void streak_reflectsConsecutivePastLogins() throws Exception {
        MockHttpSession session = registerAndLogin("test_streak", "password123");
        UserAccount user = userRepository.findByUsername("test_streak");
        LocalDate today = LocalDate.now();

        // 直近5日分（今日を含まない）の連続ログイン記録を直接投入しておく
        for (int i = 1; i <= 5; i++) {
            LoginRecord r = new LoginRecord();
            r.setUserAccount(user);
            r.setLoginDate(today.minusDays(i));
            r.setFrozen(false);
            loginRecordRepository.save(r);
        }

        // 今日分は home() へのアクセスで自動的に記録される → 合計6日連続になるはず
        mockMvc.perform(get("/").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("<span class=\"streak-number\">6</span>")));
    }

    @Test
    void claimingQuest_awardsExpOnceAndBlocksDoubleClaim() throws Exception {
        MockHttpSession session = registerAndLogin("test_quest", "password123");
        UserAccount user = userRepository.findByUsername("test_quest");

        Quest quest = questRepository.findByActiveTrueAndPeriod(Quest.Period.DAILY).stream()
                .filter(q -> q.getConditionType() == Quest.ConditionType.TASK_COMPLETE_COUNT)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("DataSeederが投入するはずのデイリークエストが見つからない"));

        Ctx taskCtx = fetchCtx("/", session);
        mockMvc.perform(post("/saveTask")
                .session(taskCtx.session)
                .param("task1", "読書").param("task1Done", "true")
                .param(taskCtx.csrf.getParameterName(), taskCtx.csrf.getToken()));

        int expAfterTask = expOf("test_quest");

        Ctx claimCtx = fetchCtx("/", session);
        mockMvc.perform(post("/claimQuest/" + quest.getId())
                        .session(claimCtx.session)
                        .param(claimCtx.csrf.getParameterName(), claimCtx.csrf.getToken()))
                .andExpect(status().is3xxRedirection());

        assertThat(expOf("test_quest")).isEqualTo(expAfterTask + quest.getRewardExp());
        assertThat(questClaimRepository.findByUserAccount(user)).hasSize(1);

        // 2回目の受け取りはブロックされ、EXPは増えない
        Ctx claimCtx2 = fetchCtx("/", session);
        mockMvc.perform(post("/claimQuest/" + quest.getId())
                        .session(claimCtx2.session)
                        .param(claimCtx2.csrf.getParameterName(), claimCtx2.csrf.getToken()))
                .andExpect(status().is3xxRedirection());

        assertThat(expOf("test_quest")).isEqualTo(expAfterTask + quest.getRewardExp());
        assertThat(questClaimRepository.findByUserAccount(user)).hasSize(1);
    }

    // ★レベルアップ演出：新しい経験値カーブ（Lv1→2は5、Lv2→3は10…と5ずつ増える累積式）で
    // レベル境界をまたいだ時だけ、リダイレクト先に levelUp=<新レベル> が付くことを確認する
    @Test
    void crossingLevelBoundary_redirectsWithLevelUpParam() throws Exception {
        MockHttpSession session = registerAndLogin("test_levelup", "password123");
        UserAccount user = userRepository.findByUsername("test_levelup");
        user.setExp(2); // レベル1（Lv2への境界は累計5EXP）。この後 fetchCtx() の初回訪問ログインボーナス+1で3、
        userRepository.save(user); // さらにTODO2件分+2で合計5となり、ちょうどレベル2の境界をまたぐ

        Ctx taskCtx = fetchCtx("/", session);
        MvcResult result = mockMvc.perform(post("/saveTask")
                        .session(taskCtx.session)
                        .param("task1", "読書").param("task1Done", "true")
                        .param("task2", "散歩").param("task2Done", "true")
                        .param(taskCtx.csrf.getParameterName(), taskCtx.csrf.getToken()))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        assertThat(expOf("test_levelup")).isEqualTo(5);
        assertThat(result.getResponse().getRedirectedUrl()).contains("levelUp=2");
    }

    // ★同じ理由の逆側：レベル境界をまたがない範囲でのEXP獲得ではlevelUpパラメータが付かないことを確認する
    @Test
    void stayingWithinSameLevel_redirectsWithoutLevelUpParam() throws Exception {
        MockHttpSession session = registerAndLogin("test_nolevelup", "password123");
        UserAccount user = userRepository.findByUsername("test_nolevelup");
        user.setExp(0); // レベル1。fetchCtx()の初回ログインボーナス+1とTODO1件分+1を足しても2で、Lv2の境界(5)には届かない
        userRepository.save(user);

        Ctx taskCtx = fetchCtx("/", session);
        MvcResult result = mockMvc.perform(post("/saveTask")
                        .session(taskCtx.session)
                        .param("task1", "読書").param("task1Done", "true")
                        .param(taskCtx.csrf.getParameterName(), taskCtx.csrf.getToken()))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        assertThat(expOf("test_nolevelup")).isEqualTo(2);
        assertThat(result.getResponse().getRedirectedUrl()).doesNotContain("levelUp");
    }

    // ★過去に発見・修正したバグの回帰テスト：パスワードリセットに成功した後、
    // 同じトークンは無効化され、二度と使い回せない（以前はdeleteByUserAccountがトランザクション不足で反映されず再利用できてしまっていた）
    @Test
    void resetPassword_invalidatesTokenAfterUse() throws Exception {
        MockHttpSession session = registerAndLogin("test_reset", "password123");
        UserAccount user = userRepository.findByUsername("test_reset");
        user.setEmail("test_reset@example.com");
        userRepository.save(user);

        Ctx forgotCtx = fetchCtx("/forgot-password", null);
        mockMvc.perform(post("/forgot-password")
                        .session(forgotCtx.session)
                        .param("email", "test_reset@example.com")
                        .param(forgotCtx.csrf.getParameterName(), forgotCtx.csrf.getToken()))
                .andExpect(status().isOk());

        PasswordResetToken resetToken = passwordResetTokenRepository.findAll().stream()
                .filter(t -> t.getUserAccount().getId().equals(user.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("トークンが発行されていない"));
        String tokenValue = resetToken.getToken();

        Ctx resetPageCtx = fetchCtx("/reset-password?token=" + tokenValue, null);
        mockMvc.perform(post("/reset-password")
                        .session(resetPageCtx.session)
                        .param("token", tokenValue)
                        .param("newPassword", "newPassword456")
                        .param(resetPageCtx.csrf.getParameterName(), resetPageCtx.csrf.getToken()))
                .andExpect(redirectedUrl("/login?resetSuccess=true"));

        // 使い終わったトークンはDBから消えているはず
        assertThat(passwordResetTokenRepository.findByToken(tokenValue)).isEmpty();

        // 同じトークンで再度GETしても「無効」画面になる
        mockMvc.perform(get("/reset-password").param("token", tokenValue))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("無効か、有効期限")));
    }

    @Test
    void friendRequestAcceptAndUnfriend_updatesLeaderboard() throws Exception {
        MockHttpSession sessionA = registerAndLogin("test_friend_a", "password123");
        MockHttpSession sessionB = registerAndLogin("test_friend_b", "password123");

        Ctx reqCtx = fetchCtx("/", sessionB);
        mockMvc.perform(post("/friends/request")
                        .session(reqCtx.session)
                        .param("username", "test_friend_a")
                        .param(reqCtx.csrf.getParameterName(), reqCtx.csrf.getToken()))
                .andExpect(redirectedUrl("/?friendRequestSent=true"));

        assertThat(friendshipRepository.findAll()).hasSize(1);
        Long friendshipId = friendshipRepository.findAll().get(0).getId();

        // Aから見て、届いている申請に表示される
        mockMvc.perform(get("/").session(sessionA))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("test_friend_b")));

        Ctx acceptCtx = fetchCtx("/", sessionA);
        mockMvc.perform(post("/friends/accept/" + friendshipId)
                        .session(acceptCtx.session)
                        .param(acceptCtx.csrf.getParameterName(), acceptCtx.csrf.getToken()))
                .andExpect(status().is3xxRedirection());

        assertThat(friendshipRepository.findById(friendshipId).orElseThrow().getStatus()).isEqualTo("ACCEPTED");

        // 承認後はリーダーボードに互いが表示される
        mockMvc.perform(get("/").session(sessionA))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("test_friend_b")));

        // ★過去に発見・修正したバグの回帰：unfriend（delete）は単発のrepository.delete()なので
        // @Transactionalなしでも動くはずだが、念のため確実に削除されることを確認しておく
        Ctx removeCtx = fetchCtx("/", sessionA);
        mockMvc.perform(post("/friends/remove/" + friendshipId)
                        .session(removeCtx.session)
                        .param(removeCtx.csrf.getParameterName(), removeCtx.csrf.getToken()))
                .andExpect(status().is3xxRedirection());

        assertThat(friendshipRepository.findById(friendshipId)).isEmpty();
    }

    @Test
    void cheeringFriend_awardsExpOnceAndBlocksSameDayDoubleCheer() throws Exception {
        MockHttpSession sessionA = registerAndLogin("test_cheer_a", "password123");
        MockHttpSession sessionB = registerAndLogin("test_cheer_b", "password123");
        UserAccount userB = userRepository.findByUsername("test_cheer_b");

        Ctx reqCtx = fetchCtx("/", sessionA);
        mockMvc.perform(post("/friends/request")
                .session(reqCtx.session)
                .param("username", "test_cheer_b")
                .param(reqCtx.csrf.getParameterName(), reqCtx.csrf.getToken()));
        Long friendshipId = friendshipRepository.findAll().get(0).getId();

        Ctx acceptCtx = fetchCtx("/", sessionB);
        mockMvc.perform(post("/friends/accept/" + friendshipId)
                .session(acceptCtx.session)
                .param(acceptCtx.csrf.getParameterName(), acceptCtx.csrf.getToken()));

        int expBefore = expOf("test_cheer_b");

        Ctx cheerCtx = fetchCtx("/", sessionA);
        mockMvc.perform(post("/friends/cheer/" + userB.getId())
                        .session(cheerCtx.session)
                        .param(cheerCtx.csrf.getParameterName(), cheerCtx.csrf.getToken()))
                .andExpect(status().is3xxRedirection());

        assertThat(expOf("test_cheer_b")).isEqualTo(expBefore + 1);

        // 同じ相手への同日2回目の応援はブロックされ、EXPは増えない
        Ctx cheerCtx2 = fetchCtx("/", sessionA);
        mockMvc.perform(post("/friends/cheer/" + userB.getId())
                        .session(cheerCtx2.session)
                        .param(cheerCtx2.csrf.getParameterName(), cheerCtx2.csrf.getToken()))
                .andExpect(status().is3xxRedirection());

        assertThat(expOf("test_cheer_b")).isEqualTo(expBefore + 1);
    }
}
