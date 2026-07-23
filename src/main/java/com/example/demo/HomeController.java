package com.example.demo;

import com.example.demo.entity.Badge;
import com.example.demo.entity.Cheer;
import com.example.demo.entity.DailyJournal;
import com.example.demo.entity.DailyTask;
import com.example.demo.entity.Friendship;
import com.example.demo.entity.LoginRecord;
import com.example.demo.entity.Quest;
import com.example.demo.entity.QuestClaim;
import com.example.demo.entity.ThemeOption;
import com.example.demo.entity.UserAccount;
import com.example.demo.repository.BadgeRepository;
import com.example.demo.repository.CheerRepository;
import com.example.demo.repository.DailyJournalRepository;
import com.example.demo.repository.DailyTaskRepository;
import com.example.demo.repository.FriendshipRepository;
import com.example.demo.repository.LoginRecordRepository;
import com.example.demo.repository.QuestClaimRepository;
import com.example.demo.repository.QuestRepository;
import com.example.demo.repository.ThemeOptionRepository;
import com.example.demo.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private static final int LOGIN_BONUS_EXP = 1;

    @Autowired private DailyJournalRepository journalRepository;
    @Autowired private DailyTaskRepository taskRepository;
    @Autowired private UserAccountRepository userRepository;
    @Autowired private LoginRecordRepository loginRecordRepository;
    @Autowired private BadgeRepository badgeRepository;
    @Autowired private ThemeOptionRepository themeOptionRepository;
    @Autowired private QuestRepository questRepository;
    @Autowired private QuestClaimRepository questClaimRepository;
    @Autowired private FriendshipRepository friendshipRepository;
    @Autowired private CheerRepository cheerRepository;
    @Autowired private LeagueService leagueService;
    @Autowired private JournalGrowthInsightService journalGrowthInsightService;

    @GetMapping("/")
    public String home(Model model, Principal principal,
                       @RequestParam(name = "loginSuccess", required = false) String loginSuccess,
                       @RequestParam(name = "error", required = false) String error,
                       @RequestParam(name = "notAdmin", required = false) String notAdmin,
                       @RequestParam(name = "levelUp", required = false) Integer levelUpParam) {

        UserAccount currentUser = userRepository.findByUsername(principal.getName());

        // --- 初回ログインならまずオンボーディングへ ---
        if (!currentUser.isOnboardingCompleted()) {
            return "redirect:/onboarding";
        }

        LocalDate today = LocalDate.now();

        // --- ★カムバック（休眠復帰）施策：前回訪問からの経過日数を先に見ておく ---
        List<LoginRecord> priorLoginRecords = loginRecordRepository.findByUserAccount(currentUser);
        LocalDate lastVisitDate = priorLoginRecords.stream()
                .filter(r -> !r.isFrozen())
                .map(LoginRecord::getLoginDate)
                .filter(d -> d.isBefore(today))
                .max(LocalDate::compareTo)
                .orElse(null);
        Integer daysSinceLastVisit = lastVisitDate != null ? (int) ChronoUnit.DAYS.between(lastVisitDate, today) : null;
        model.addAttribute("comebackMessage", buildComebackMessage(daysSinceLastVisit));

        // --- ログインボーナス：起動ごとに最初の1回だけ付与 ---
        boolean isFirstVisitToday = !loginRecordRepository.existsByUserAccountAndLoginDate(currentUser, today);
        Integer loginBonusLevelUp = null;
        if (isFirstVisitToday) {
            LoginRecord record = new LoginRecord();
            record.setUserAccount(currentUser);
            record.setLoginDate(today);
            loginRecordRepository.save(record);
            int levelBeforeBonus = levelOf(currentUser.getExp());
            currentUser.setExp(currentUser.getExp() + LOGIN_BONUS_EXP);
            int levelAfterBonus = levelOf(currentUser.getExp());
            if (levelAfterBonus > levelBeforeBonus) loginBonusLevelUp = levelAfterBonus;
        }

        // --- ストリークフリーズ（お休みチケット）：週1回まで、残数は毎週月曜にリセット ---
        LocalDate mondayOfThisWeek = today.with(DayOfWeek.MONDAY);
        if (currentUser.getFreezeWeekStart() == null || !currentUser.getFreezeWeekStart().equals(mondayOfThisWeek)) {
            currentUser.setFreezeWeekStart(mondayOfThisWeek);
            currentUser.setStreakFreezeRemaining(1);
        }

        LocalDate yesterday = today.minusDays(1);
        LocalDate dayBeforeYesterday = today.minusDays(2);
        boolean usedStreakFreeze = false;
        if (!loginRecordRepository.existsByUserAccountAndLoginDate(currentUser, yesterday)
                && loginRecordRepository.existsByUserAccountAndLoginDate(currentUser, dayBeforeYesterday)
                && currentUser.getStreakFreezeRemaining() > 0) {
            LoginRecord freezeRecord = new LoginRecord();
            freezeRecord.setUserAccount(currentUser);
            freezeRecord.setLoginDate(yesterday);
            freezeRecord.setFrozen(true);
            loginRecordRepository.save(freezeRecord);
            currentUser.setStreakFreezeRemaining(currentUser.getStreakFreezeRemaining() - 1);
            usedStreakFreeze = true;
        }

        userRepository.save(currentUser);

        int streak = calculateStreak(currentUser, today);
        model.addAttribute("streak", streak);
        model.addAttribute("streakFreezeRemaining", currentUser.getStreakFreezeRemaining());
        model.addAttribute("usedStreakFreeze", usedStreakFreeze);

        // ★ウェルカムポップアップは1日1回だけ（その日最初の訪問時のみ）
        if (loginSuccess != null && isFirstVisitToday) model.addAttribute("showWelcomePopup", true);
        model.addAttribute("loginBonusExp", isFirstVisitToday ? LOGIN_BONUS_EXP : 0);
        model.addAttribute("notAdmin", notAdmin != null);

        int totalExp = currentUser.getExp();
        LevelingUtil.LevelInfo levelInfo = LevelingUtil.compute(totalExp);
        int level = levelInfo.level();
        model.addAttribute("level", level);
        model.addAttribute("expProgress", levelInfo.expIntoLevel());
        model.addAttribute("expRequired", levelInfo.expRequiredForLevel());
        model.addAttribute("expProgressPercent", levelInfo.progressPercent());
        model.addAttribute("isAdmin", "ADMIN".equals(currentUser.getRole()));
        model.addAttribute("identityDeclaration", currentUser.getIdentityDeclaration());
        model.addAttribute("userEmail", currentUser.getEmail());
        // ★レベルアップ演出：ログインボーナスでの昇格を優先し、なければ各アクションのリダイレクトパラメータを見る
        model.addAttribute("levelUpTo", loginBonusLevelUp != null ? loginBonusLevelUp : levelUpParam);

        // --- ★Lv30背景：カスタム画像のアップロード状況 ---
        model.addAttribute("backgroundUnlockLevel", 30);
        model.addAttribute("customBackgroundUrl", currentUser.getCustomBackgroundFilename() != null
                ? "/backgrounds/" + currentUser.getCustomBackgroundFilename() : null);
        model.addAttribute("notificationFrequency", currentUser.getNotificationFrequency());

        // --- ユーザー自身の記録のみ取得 ---
        // ★履歴表示は日付の新しい順。idはレコード作成順であって日付順とは限らない
        // （例：streak freezeやデータ移行等でid順と日付順がずれるケース）ため、dateを主キーにソートする。
        Sort byDateDesc = Sort.by(Sort.Direction.DESC, "date").and(Sort.by(Sort.Direction.DESC, "id"));
        List<DailyTask> myTasks = taskRepository.findByUserAccount(currentUser, byDateDesc);
        List<DailyJournal> myJournals = journalRepository.findByUserAccount(currentUser, byDateDesc);
        List<LoginRecord> myLoginRecords = loginRecordRepository.findByUserAccount(currentUser);

        // --- 累計達成日数（ストリークが途切れても積み上げを可視化） ---
        int cumulativeDays = myLoginRecords.size();
        LocalDate baseDate = currentUser.getCreatedAt();
        if (baseDate == null) {
            baseDate = myLoginRecords.stream().map(LoginRecord::getLoginDate).min(LocalDate::compareTo).orElse(today);
            currentUser.setCreatedAt(baseDate);
            userRepository.save(currentUser);
        }
        model.addAttribute("cumulativeDays", cumulativeDays);

        // ★今日のアクション入力欄の下書き（未チェックのまま入力した文字列）をブラウザのlocalStorageに
        // 日付付きキーで保存させるために、日付をそのままJSへ渡しておく
        model.addAttribute("todayIso", today.toString());

        // ★今日のタスク状態を先に取得しておく（達成率・taskForm表示の両方で使うため）
        Optional<DailyTask> todayTaskOpt = taskRepository.findByUserAccountAndDate(currentUser, today);
        DailyTask todayTask = todayTaskOpt.orElse(null);
        Optional<DailyJournal> todayJournalOpt = journalRepository.findByUserAccountAndDate(currentUser, today);
        DailyJournal todayJournal = todayJournalOpt.orElse(null);
        model.addAttribute("isJournalDoneToday", todayJournalOpt.isPresent());

        // ★各項目はチェックした瞬間に個別ロックされる仕様のため、既に完了した項目はその日の保存済みテキストを、
        // まだの項目は編集できるよう固定習慣のプレースホルダーを表示用に組み立てる（永続化はしない使い捨てオブジェクト）
        DailyTask taskForm = new DailyTask();
        boolean task1DoneToday = todayTask != null && todayTask.isTask1Done();
        boolean task2DoneToday = todayTask != null && todayTask.isTask2Done();
        boolean task3DoneToday = todayTask != null && todayTask.isTask3Done();
        boolean badHabitDoneToday = todayTask != null && todayTask.isBadHabitDone();
        taskForm.setTask1Done(task1DoneToday);
        taskForm.setTask1(task1DoneToday ? todayTask.getTask1() : currentUser.getFixedHabit1());
        taskForm.setTask2Done(task2DoneToday);
        taskForm.setTask2(task2DoneToday ? todayTask.getTask2() : currentUser.getFixedHabit2());
        taskForm.setTask3Done(task3DoneToday);
        taskForm.setTask3(task3DoneToday ? todayTask.getTask3() : currentUser.getFixedHabit3());
        taskForm.setBadHabitDone(badHabitDoneToday);
        taskForm.setBadHabit(badHabitDoneToday ? todayTask.getBadHabit() : currentUser.getFixedBadHabit());
        model.addAttribute("taskForm", taskForm);

        // --- ★達成率：これまで入力されたアクション数のうち、実際にチェックできた割合を日をまたいで累積
        // （防衛クエストは対象外）。チェックが1つも入っていない状態（入力数0）は0%とする ---
        model.addAttribute("achievementRate", computeAchievementRate(currentUser, myTasks, todayTask, today));
        // ★今日の分はブラウザ側で画面の実際の入力内容を見て正確に数え、この過去分に上乗せする
        // （サーバー側は今日まだ未チェックの自由入力テキストを知る手段がないため）
        int[] historicalCounts = computeHistoricalAchievementCounts(myTasks, today);
        model.addAttribute("historicalInputCount", historicalCounts[0]);
        model.addAttribute("historicalCompletedCount", historicalCounts[1]);

        // --- 実績（バッジ）：DB管理の定義を条件判定。獲得済み一覧と、未獲得も含む進捗一覧の両方を用意 ---
        int taskCompleteTotal = myTasks.stream().mapToInt(this::countDone).sum();
        List<Badge> earnedBadges = new ArrayList<>();
        List<BadgeProgressView> allBadgeViews = new ArrayList<>();
        for (Badge b : badgeRepository.findByActiveTrue()) {
            int current = badgeCurrentValue(b, level, streak, myJournals.size(), taskCompleteTotal);
            boolean earned = current >= b.getConditionValue();
            allBadgeViews.add(new BadgeProgressView(b, current, earned));
            if (earned) earnedBadges.add(b);
        }
        model.addAttribute("badges", earnedBadges);
        model.addAttribute("allBadges", allBadgeViews);

        // --- カラーテーマ（DB管理） ---
        model.addAttribute("themeOptions", themeOptionRepository.findByActiveTrueOrderByUnlockLevelAsc());

        model.addAttribute("userGoal", currentUser.getLongTermGoal());
        model.addAttribute("fixedHabit1", currentUser.getFixedHabit1());
        model.addAttribute("fixedHabit2", currentUser.getFixedHabit2());
        model.addAttribute("fixedHabit3", currentUser.getFixedHabit3());
        model.addAttribute("fixedBadHabit", currentUser.getFixedBadHabit());
        model.addAttribute("currentTheme", currentUser.getTheme() != null ? currentUser.getTheme() : "default");

        // --- ★今週の成長グラフ：実際のEXP獲得実績から計算（以前はランダムのダミー値だった） ---
        model.addAttribute("chartData", computeLast7DaysExp(currentUser, today, myTasks, myJournals, myLoginRecords));

        // --- ★気分推移グラフ：直近14日間の実際の気分スコア ---
        LocalDate moodStart = today.minusDays(13);
        List<String> moodLabels = new ArrayList<>();
        for (int i = 0; i < 14; i++) moodLabels.add(moodStart.plusDays(i).toString().substring(5));
        model.addAttribute("moodTrendLabels", moodLabels);
        model.addAttribute("moodTrendValues", computeMoodTrend(myJournals, today));

        // --- ★モンスター戦闘演出：撃破した体数に応じてHPが倍増していく累積ダメージをモデルに反映 ---
        addMonsterState(model, currentUser);

        // --- ★デイリー／ウィークリークエスト ---
        List<DailyTask> tasksThisWeek = myTasks.stream().filter(t -> !t.getDate().isBefore(mondayOfThisWeek)).collect(Collectors.toList());
        List<DailyJournal> journalsThisWeek = myJournals.stream().filter(j -> !j.getDate().isBefore(mondayOfThisWeek)).collect(Collectors.toList());

        List<QuestProgressView> dailyQuests = buildQuestViews(Quest.Period.DAILY, currentUser, today.toString(),
                todayTask, todayJournal, tasksThisWeek, journalsThisWeek, streak);
        List<QuestProgressView> weeklyQuests = buildQuestViews(Quest.Period.WEEKLY, currentUser, mondayOfThisWeek.toString(),
                todayTask, todayJournal, tasksThisWeek, journalsThisWeek, streak);
        model.addAttribute("dailyQuests", dailyQuests);
        model.addAttribute("weeklyQuests", weeklyQuests);

        // ★受け取り忘れ防止：達成済み未受取のクエストがあればクエストタブに！バッジを出す
        boolean hasUnclaimedQuest = dailyQuests.stream().anyMatch(q -> q.isCompleted() && !q.isClaimed())
                || weeklyQuests.stream().anyMatch(q -> q.isCompleted() && !q.isClaimed());
        model.addAttribute("hasUnclaimedQuest", hasUnclaimedQuest);

        model.addAttribute("journalForm", new DailyJournal());

        model.addAttribute("journals", myJournals);
        model.addAttribute("tasks", myTasks);

        // ★AIジャーナル成長分析：直近30日分の記録から前向きなコメントを生成（1日1回だけキャッシュ）
        model.addAttribute("growthInsight", journalGrowthInsightService.getOrGenerate(currentUser, today));

        Map<String, Integer> activityMap = new HashMap<>();
        for (LoginRecord r : myLoginRecords) {
            String date = r.getLoginDate().toString();
            activityMap.put(date, activityMap.getOrDefault(date, 0) + 1);
        }
        for (DailyTask t : myTasks) {
            int count = countDone(t);
            if (count > 0) {
                String date = t.getDate().toString();
                activityMap.put(date, activityMap.getOrDefault(date, 0) + count);
            }
        }
        for (DailyJournal j : myJournals) {
            String date = j.getDate().toString();
            activityMap.put(date, activityMap.getOrDefault(date, 0) + 2);
        }
        model.addAttribute("activityMap", activityMap);

        addFriendState(model, currentUser, today);
        addLeagueState(model, currentUser);

        return "index";
    }

    // ★週間リーグ（Duolingo風）：自分の階層名と、同じ階層内の週間EXPランキングをモデルに積む
    private void addLeagueState(Model model, UserAccount currentUser) {
        List<UserAccount> standings = leagueService.standingsFor(currentUser);
        List<Map<String, Object>> rows = new ArrayList<>();
        for (UserAccount u : standings) {
            Map<String, Object> row = new HashMap<>();
            row.put("username", u.getUsername());
            row.put("weeklyExp", leagueService.weeklyExpOf(u));
            row.put("isSelf", u.getId().equals(currentUser.getId()));
            rows.add(row);
        }
        model.addAttribute("leagueTierName", LeagueService.tierName(currentUser.getLeagueTier()));
        model.addAttribute("leagueTierIndex", currentUser.getLeagueTier());
        model.addAttribute("leagueTierCount", LeagueService.TIER_NAMES.length);
        model.addAttribute("leagueStandings", rows);
    }

    // ★フレンドタブ用のデータ一式：受信/送信中の申請、成立済みフレンド、レベル・ストリークによるリーダーボード
    private void addFriendState(Model model, UserAccount currentUser, LocalDate today) {
        List<Friendship> incoming = friendshipRepository.findByAddresseeAndStatus(currentUser, "PENDING");
        List<Friendship> outgoing = friendshipRepository.findByRequesterAndStatus(currentUser, "PENDING");
        List<Friendship> accepted = friendshipRepository.findAcceptedByUser(currentUser);

        model.addAttribute("incomingFriendRequests", incoming);
        model.addAttribute("outgoingFriendRequests", outgoing);
        model.addAttribute("hasFriendRequests", !incoming.isEmpty());

        List<UserAccount> leaderboardUsers = new ArrayList<>();
        leaderboardUsers.add(currentUser);
        for (Friendship f : accepted) {
            UserAccount friend = f.getRequester().getId().equals(currentUser.getId()) ? f.getAddressee() : f.getRequester();
            leaderboardUsers.add(friend);
        }

        // ★N+1対策：行ごとに「応援済みか」「何人から応援されたか」を問い合わせるのではなく、
        // 「自分が今日送った応援」と「リーダーボード全員が今日受け取った応援」をそれぞれ1回のクエリで取得し、
        // メモリ上のMap/Setで引く（人数が増えてもクエリ件数が2件のまま増えないようにする）。
        Set<Long> alreadyCheeredUserIds = cheerRepository.findByFromUserAndCheerDate(currentUser, today).stream()
                .map(c -> c.getToUser().getId())
                .collect(Collectors.toSet());
        List<Cheer> cheersReceivedTodayByLeaderboard = cheerRepository.findByToUserInAndCheerDate(leaderboardUsers, today);
        Map<Long, Long> cheerCountByUserId = cheersReceivedTodayByLeaderboard.stream()
                .collect(Collectors.groupingBy(c -> c.getToUser().getId(), Collectors.counting()));

        List<Map<String, Object>> leaderboard = new ArrayList<>();
        for (UserAccount u : leaderboardUsers) {
            boolean isSelf = u.getId().equals(currentUser.getId());
            int cheersReceived = cheerCountByUserId.getOrDefault(u.getId(), 0L).intValue();
            leaderboard.add(friendLeaderboardRow(u, today, isSelf, alreadyCheeredUserIds.contains(u.getId()), cheersReceived));
        }
        leaderboard.sort((a, b) -> {
            int levelCompare = Integer.compare((int) b.get("level"), (int) a.get("level"));
            if (levelCompare != 0) return levelCompare;
            return Integer.compare((int) b.get("exp"), (int) a.get("exp"));
        });
        model.addAttribute("friendLeaderboard", leaderboard);

        // ★今日自分を応援してくれた人の一覧（みんチャレ/Strava風のkudos表示）。
        // 上で取得済みのcheersReceivedTodayByLeaderboardから自分宛てだけ絞り込めば、追加クエリ不要。
        List<Cheer> cheersReceivedToday = cheersReceivedTodayByLeaderboard.stream()
                .filter(c -> c.getToUser().getId().equals(currentUser.getId()))
                .collect(Collectors.toList());
        model.addAttribute("cheersReceivedToday", cheersReceivedToday);
    }

    private Map<String, Object> friendLeaderboardRow(UserAccount user, LocalDate today, boolean isSelf,
                                                       boolean alreadyCheeredToday, int cheersReceivedToday) {
        Map<String, Object> row = new HashMap<>();
        row.put("userId", user.getId());
        row.put("username", user.getUsername());
        row.put("level", levelOf(user.getExp()));
        row.put("exp", user.getExp());
        row.put("streak", calculateStreak(user, today));
        row.put("isSelf", isSelf);
        row.put("alreadyCheeredToday", !isSelf && alreadyCheeredToday);
        row.put("cheersReceivedToday", cheersReceivedToday);
        return row;
    }

    private static final Path JOURNAL_PHOTO_DIR = Path.of("uploads", "journal-photos");

    @PostMapping("/saveJournal")
    public String saveJournal(@ModelAttribute DailyJournal journalForm,
                               @RequestParam(value = "photo", required = false) MultipartFile photo,
                               Principal principal) {
        UserAccount currentUser = userRepository.findByUsername(principal.getName());
        LocalDate today = LocalDate.now();
        if (journalRepository.findByUserAccountAndDate(currentUser, today).isPresent()) return "redirect:/";

        int earnedExp = 0;
        if (journalForm.getAchievement() != null && !journalForm.getAchievement().trim().isEmpty()) earnedExp += 1;
        if (journalForm.getGratitude1() != null && !journalForm.getGratitude1().trim().isEmpty()) earnedExp += 1;
        if (journalForm.getGratitude2() != null && !journalForm.getGratitude2().trim().isEmpty()) earnedExp += 1;
        if (journalForm.getGratitude3() != null && !journalForm.getGratitude3().trim().isEmpty()) earnedExp += 1;
        if (journalForm.getDiaryText() != null && !journalForm.getDiaryText().trim().isEmpty()) earnedExp += 1;
        if (journalForm.getMoodScore() >= 1 && journalForm.getMoodScore() <= 5) earnedExp += 1;

        if (earnedExp == 0) return "redirect:/?error=journalEmpty";

        // ★写真は任意添付。拡張子や申告されたContent-Typeを信用せず、実際に画像として読めるかで検証し、
        // 読み込めた画像をPNGとして描き直して保存することで偽装ファイルを無害化する（背景アップロードと同じ考え方）。
        // 写真だけ失敗しても（例：iPhoneのHEIC形式など非対応フォーマット）、せっかく書いた本文まで
        // 巻き添えで消えてしまわないよう、本文の保存自体は続行し、写真だけスキップしてエラーを伝える。
        String photoError = null;
        if (photo != null && !photo.isEmpty()) {
            try {
                // readSafelyはヘッダの解像度を先にチェックし、展開爆弾を本デコード前に弾く
                BufferedImage image = ImageUploadUtil.readSafely(photo.getInputStream());
                if (image == null) {
                    photoError = "journalPhotoInvalid";
                } else {
                    Files.createDirectories(JOURNAL_PHOTO_DIR);
                    String filename = "journal-" + currentUser.getId() + "-" + today + ".png";
                    File destination = JOURNAL_PHOTO_DIR.resolve(filename).toFile();
                    ImageIO.write(image, "png", destination);
                    journalForm.setPhotoFilename(filename);
                }
            } catch (ImageUploadUtil.TooLargeException e) {
                photoError = "journalPhotoTooLargeDimensions";
            } catch (IOException e) {
                photoError = "journalPhotoFailed";
            }
        }

        int levelBefore = levelOf(currentUser.getExp());
        currentUser.setExp(currentUser.getExp() + earnedExp);
        int levelAfter = levelOf(currentUser.getExp());
        userRepository.save(currentUser);
        journalForm.setDate(today);
        journalForm.setUserAccount(currentUser);
        journalRepository.save(journalForm);
        String levelUpQuery = levelAfter > levelBefore ? "levelUp=" + levelAfter : null;
        if (photoError != null) return "redirect:/?error=" + photoError;
        return levelUpQuery != null ? "redirect:/?" + levelUpQuery : "redirect:/";
    }

    // ★チェックを入れた瞬間にダメージ・EXPが確定する1項目単位のAPI。一度チェックした項目は
    // ロックされ、以後同じ項目を再度この経路で完了させることはできない（EXP/ボスダメージの整合性を守るため、
    // 過去回のセッションで「保存後は変更・削除できない」方針にした流れと同じ考え方）。
    public static class TaskItemRequest {
        public String slot; // "task1" | "task2" | "task3" | "badHabit"
        public String text;
    }

    private static final Set<String> TASK_ITEM_SLOTS = Set.of("task1", "task2", "task3", "badHabit");

    @PostMapping("/toggleTaskItem")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleTaskItem(
            @RequestBody TaskItemRequest req, Principal principal) {
        UserAccount currentUser = userRepository.findByUsername(principal.getName());
        LocalDate today = LocalDate.now();
        String text = req.text == null ? "" : req.text.trim();

        if (req.slot == null || !TASK_ITEM_SLOTS.contains(req.slot) || text.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        DailyTask task = taskRepository.findByUserAccountAndDate(currentUser, today).orElseGet(() -> {
            DailyTask t = new DailyTask();
            t.setUserAccount(currentUser);
            t.setDate(today);
            return t;
        });

        boolean alreadyDone = switch (req.slot) {
            case "task1" -> task.isTask1Done();
            case "task2" -> task.isTask2Done();
            case "task3" -> task.isTask3Done();
            default -> task.isBadHabitDone();
        };
        if (alreadyDone) {
            return ResponseEntity.status(409).build(); // 既にロック済みの項目への再チェック要求
        }

        switch (req.slot) {
            case "task1" -> { task.setTask1(text); task.setTask1Done(true); }
            case "task2" -> { task.setTask2(text); task.setTask2Done(true); }
            case "task3" -> { task.setTask3(text); task.setTask3Done(true); }
            default -> { task.setBadHabit(text); task.setBadHabitDone(true); }
        }

        // ★達成率の分母（inputCount）をこのタイミングで確定・上書きしておく。「固定習慣の非空数」と
        // 「現時点でのチェック済み数」の大きい方を採用することで、固定習慣が無い項目に自由入力して
        // チェックした場合でも分母がチェック数を下回らないようにする。日が変わって過去日として
        // 集計される時も、この値がそのまま「その日は何個入力欄があったか」の記録として残る
        int completedSoFar = (task.isTask1Done() ? 1 : 0) + (task.isTask2Done() ? 1 : 0) + (task.isTask3Done() ? 1 : 0);
        int fixedHabitCount = 0;
        if (currentUser.getFixedHabit1() != null && !currentUser.getFixedHabit1().isBlank()) fixedHabitCount++;
        if (currentUser.getFixedHabit2() != null && !currentUser.getFixedHabit2().isBlank()) fixedHabitCount++;
        if (currentUser.getFixedHabit3() != null && !currentUser.getFixedHabit3().isBlank()) fixedHabitCount++;
        task.setInputCount(Math.max(fixedHabitCount, completedSoFar));

        taskRepository.save(task);

        int levelBefore = levelOf(currentUser.getExp());
        currentUser.setExp(currentUser.getExp() + 1);
        int levelAfter = levelOf(currentUser.getExp());

        // ★防御（やらない事）はボスへのダメージ対象外。TODO（task1〜3）のみ1ダメージ
        boolean isDamagingSlot = !"badHabit".equals(req.slot);
        int damage = isDamagingSlot ? 1 : 0;
        boolean monsterDefeated = false;
        if (isDamagingSlot) {
            int accumulated = currentUser.getMonsterDamageAccumulated() + 1;
            int monsterTier = currentUser.getMonsterTier();
            while (accumulated >= monsterHpMaxForTier(monsterTier)) {
                accumulated -= (int) monsterHpMaxForTier(monsterTier);
                monsterTier++;
                monsterDefeated = true;
            }
            currentUser.setMonsterTier(monsterTier);
            currentUser.setMonsterDamageAccumulated(accumulated);
        }
        userRepository.save(currentUser);

        long hpMax = monsterHpMaxForTier(currentUser.getMonsterTier());
        long hpCurrent = Math.max(0, hpMax - currentUser.getMonsterDamageAccumulated());
        LevelingUtil.LevelInfo levelInfo = LevelingUtil.compute(currentUser.getExp());

        Map<String, Object> body = new HashMap<>();
        body.put("damage", damage);
        body.put("monsterDefeated", monsterDefeated);
        body.put("monsterTier", currentUser.getMonsterTier());
        body.put("monsterSprite", MONSTER_SPRITES[(currentUser.getMonsterTier() - 1) % MONSTER_SPRITES.length]);
        body.put("monsterHpMax", hpMax);
        body.put("monsterHpCurrent", hpCurrent);
        body.put("monsterHpPercent", (int) Math.round(hpCurrent * 100.0 / hpMax));
        body.put("levelUp", levelAfter > levelBefore ? levelAfter : null);
        body.put("level", levelInfo.level());
        body.put("expProgress", levelInfo.expIntoLevel());
        body.put("expRequired", levelInfo.expRequiredForLevel());
        body.put("expProgressPercent", levelInfo.progressPercent());
        // ★達成率は返さない：今日の分はクライアント側が画面の実際の入力内容（自由入力の下書きも含む）を
        // 見て正確に計算し、ページ読み込み時に渡しておいた過去分の累計に上乗せする
        return ResponseEntity.ok(body);
    }

    // ★達成率＝これまで入力されてきたTODO（task1〜3、防衛クエストは対象外）のうち、実際にチェックできた
    // 割合を日をまたいで累積していく（1日目は0%からスタートし、2日目以降も積み上がっていく）。
    // 過去の日については「toggleTaskItemで保存された時点で既にチェック済み」なので入力数=チェック数だが、
    // 万が一チェックせず未完了のまま残ったレコード（旧saveTask時代の一括保存分など）もそのまま正しく数えられる。
    // 今日の分だけは、まだ未チェックでもDBに保存されない（固定習慣のプレースホルダー止まり）ため、
    // 別枠でtodayTask＋固定習慣を見て「今日時点で入力されている項目数」を数える。
    // ★達成率の「今日を除く過去分」の集計だけを行う。今日の分をここに含めないのは、
    // サーバー側は今日まだチェックしていない項目に何が入力途中か（固定習慣と違うカスタムな
    // 自由入力など）を知る手段がなく、正確に数えられないため。今日の分はブラウザ側で
    // 実際に画面に入力されている内容を見て数え、この過去分の数字に上乗せする
    // （index.htmlのupdateAchievementRateDisplay()を参照）。
    private int[] computeHistoricalAchievementCounts(List<DailyTask> myTasks, LocalDate today) {
        int inputCount = 0;
        int completedCount = 0;
        for (DailyTask t : myTasks) {
            if (t.getDate().equals(today)) continue;
            int rowCompleted = (t.isTask1Done() ? 1 : 0) + (t.isTask2Done() ? 1 : 0) + (t.isTask3Done() ? 1 : 0);
            int rowInput;
            if (t.getInputCount() != null) {
                // ★inputCountが記録済みのレコード（この機能追加以降にチェックが発生した日）はそのまま使う。
                // これにより「3つ入力欄があったのに1つしかチェックしなかった」という事実が、日をまたいだ後も
                // 分母から消えずに残る（未チェックの項目自体はDBに一切保存されないため、これが無いと復元できない）
                rowInput = t.getInputCount();
            } else {
                // ★この機能を追加する前の過去データ用フォールバック：非空フィールド数で代用
                rowInput = 0;
                if (t.getTask1() != null && !t.getTask1().isBlank()) rowInput++;
                if (t.getTask2() != null && !t.getTask2().isBlank()) rowInput++;
                if (t.getTask3() != null && !t.getTask3().isBlank()) rowInput++;
            }
            inputCount += rowInput;
            completedCount += rowCompleted;
        }
        return new int[]{inputCount, completedCount};
    }

    // ★達成率の初期表示（JS無効時やページの初回レンダリング用）のフォールバック計算。
    // 今日の分は、今日まだ1つもチェックしていない（todayTaskがまだ存在しない）うちは
    // 固定習慣のプレースホルダーを分母に加えない（日をまたいだ直後に前日の達成率が
    // 本人操作なしで下がって見えるのを防ぐため）。実際の画面表示はJS側のライブ計算で
    // 上書きされる（カスタムな自由入力もそちらでは正しく拾える）。
    private int computeAchievementRate(UserAccount user, List<DailyTask> myTasks, DailyTask todayTask, LocalDate today) {
        int[] historical = computeHistoricalAchievementCounts(myTasks, today);
        int inputCount = historical[0];
        int completedCount = historical[1];
        if (todayTask != null) {
            boolean t1Done = todayTask.isTask1Done();
            String t1 = t1Done ? todayTask.getTask1() : user.getFixedHabit1();
            if (t1 != null && !t1.isBlank()) { inputCount++; if (t1Done) completedCount++; }
            boolean t2Done = todayTask.isTask2Done();
            String t2 = t2Done ? todayTask.getTask2() : user.getFixedHabit2();
            if (t2 != null && !t2.isBlank()) { inputCount++; if (t2Done) completedCount++; }
            boolean t3Done = todayTask.isTask3Done();
            String t3 = t3Done ? todayTask.getTask3() : user.getFixedHabit3();
            if (t3 != null && !t3.isBlank()) { inputCount++; if (t3Done) completedCount++; }
        }
        return inputCount > 0 ? (int) Math.round(completedCount * 100.0 / inputCount) : 0;
    }

    @PostMapping("/claimQuest/{id}")
    public String claimQuest(@PathVariable Long id, Principal principal) {
        UserAccount currentUser = userRepository.findByUsername(principal.getName());
        Quest quest = questRepository.findById(id).orElse(null);
        if (quest == null || !quest.isActive()) return "redirect:/";

        LocalDate today = LocalDate.now();
        LocalDate mondayOfThisWeek = today.with(DayOfWeek.MONDAY);
        String periodKey = quest.getPeriod() == Quest.Period.DAILY ? today.toString() : mondayOfThisWeek.toString();

        if (questClaimRepository.findByUserAccountAndQuestAndPeriodKey(currentUser, quest, periodKey).isPresent()) {
            return "redirect:/"; // 受け取り済み
        }

        // サーバー側で達成条件を再判定してから付与する（不正リクエスト対策）
        int streak = calculateStreak(currentUser, today);
        Sort byDateDesc = Sort.by(Sort.Direction.DESC, "date").and(Sort.by(Sort.Direction.DESC, "id"));
        List<DailyTask> myTasks = taskRepository.findByUserAccount(currentUser, byDateDesc);
        List<DailyJournal> myJournals = journalRepository.findByUserAccount(currentUser, byDateDesc);
        List<DailyTask> tasksThisWeek = myTasks.stream().filter(t -> !t.getDate().isBefore(mondayOfThisWeek)).collect(Collectors.toList());
        List<DailyJournal> journalsThisWeek = myJournals.stream().filter(j -> !j.getDate().isBefore(mondayOfThisWeek)).collect(Collectors.toList());
        DailyTask todayTask = taskRepository.findByUserAccountAndDate(currentUser, today).orElse(null);
        DailyJournal todayJournal = journalRepository.findByUserAccountAndDate(currentUser, today).orElse(null);

        int current = computeQuestCurrent(quest, todayTask, todayJournal, tasksThisWeek, journalsThisWeek, streak);
        if (current >= quest.getTargetCount()) {
            int levelBefore = levelOf(currentUser.getExp());
            currentUser.setExp(currentUser.getExp() + quest.getRewardExp());
            int levelAfter = levelOf(currentUser.getExp());
            userRepository.save(currentUser);
            QuestClaim claim = new QuestClaim();
            claim.setUserAccount(currentUser);
            claim.setQuest(quest);
            claim.setPeriodKey(periodKey);
            questClaimRepository.save(claim);
            if (levelAfter > levelBefore) return "redirect:/?levelUp=" + levelAfter;
        }
        return "redirect:/";
    }

    // ============================ ここから内部ヘルパー ============================
    // ★アクション・ジャーナルの削除機能は廃止した（EXPやボスへの累積ダメージは記録削除後も
    // 取り消されず、履歴の件数を見て判定するクエスト・実績の進捗と食い違ってしまうため）。
    // 代わりに保存前に確認ポップアップを出し、登録内容は事後に変更できない前提にしている。

    private int levelOf(int exp) {
        return LevelingUtil.levelOf(exp);
    }

    private int calculateStreak(UserAccount user, LocalDate today) {
        int streak = 0;
        LocalDate checkDate = today;
        while (loginRecordRepository.existsByUserAccountAndLoginDate(user, checkDate)) {
            streak++;
            checkDate = checkDate.minusDays(1);
        }
        return streak;
    }

    private int countDone(DailyTask t) {
        int c = 0;
        if (t.isTask1Done()) c++;
        if (t.isTask2Done()) c++;
        if (t.isTask3Done()) c++;
        if (t.isBadHabitDone()) c++;
        return c;
    }

    private int badgeCurrentValue(Badge b, int level, int streak, int journalCount, int taskCompleteTotal) {
        return switch (b.getConditionType()) {
            case LEVEL -> level;
            case STREAK -> streak;
            case JOURNAL_COUNT -> journalCount;
            case TASK_COUNT -> taskCompleteTotal;
        };
    }

    // 前回訪問からの経過日数に応じた、責めないトーンの復帰メッセージ（要件設計書6.6）
    private String buildComebackMessage(Integer daysSinceLastVisit) {
        if (daysSinceLastVisit == null) return null;
        if (daysSinceLastVisit >= 30) {
            return "🌱 " + daysSinceLastVisit + "日ぶりですね。焦らなくて大丈夫。また今日から、無理のない範囲で一緒に再スタートしましょう！";
        }
        if (daysSinceLastVisit >= 14) {
            return "👋 " + daysSinceLastVisit + "日ぶりの再開、お待ちしていました！ゼロからじゃなく、ここからで大丈夫です。";
        }
        if (daysSinceLastVisit >= 7) {
            return "🌤️ " + daysSinceLastVisit + "日ぶりですね。今日からまたコツコツ積み上げていきましょう！";
        }
        if (daysSinceLastVisit >= 3) {
            return "😊 お久しぶりです！また今日から再開しましょう。";
        }
        return null;
    }

    // ★見た目を3種類ループさせる（1体目=👹, 2体目=👺, 3体目=🐉, 4体目=👹, ...）
    private static final String[] MONSTER_SPRITES = {"👹", "👺", "🐉"};

    // ★モンスターのHPを model に積む。1体目はHP3、撃破するたびに次の体はHPが2倍になっていく。
    // ダメージは日をまたいで累積し（toggleTaskItemで加算・撃破判定済み）、ここでは現在の体の状態を表示するだけ
    private void addMonsterState(Model model, UserAccount currentUser) {
        int monsterTier = currentUser.getMonsterTier();
        long monsterHpMax = monsterHpMaxForTier(monsterTier);
        int accumulated = currentUser.getMonsterDamageAccumulated();
        long monsterHpCurrent = Math.max(0, monsterHpMax - accumulated);
        boolean monsterDefeated = accumulated >= monsterHpMax;
        model.addAttribute("monsterTier", monsterTier);
        model.addAttribute("monsterSprite", MONSTER_SPRITES[(monsterTier - 1) % MONSTER_SPRITES.length]);
        model.addAttribute("monsterHpMax", monsterHpMax);
        model.addAttribute("monsterHpCurrent", monsterHpCurrent);
        model.addAttribute("monsterHpPercent", (int) Math.round(monsterHpCurrent * 100.0 / monsterHpMax));
        model.addAttribute("monsterDefeated", monsterDefeated);
    }

    // ★n体目のモンスターのHP = 3 * 2^(n-1)（1体目=3, 2体目=6, 3体目=12, ...）
    private static long monsterHpMaxForTier(int tier) {
        return 3L << (tier - 1);
    }

    private List<QuestProgressView> buildQuestViews(Quest.Period period, UserAccount currentUser, String periodKey,
            DailyTask todayTask, DailyJournal todayJournal, List<DailyTask> tasksThisWeek, List<DailyJournal> journalsThisWeek, int streak) {
        List<QuestProgressView> views = new ArrayList<>();
        for (Quest q : questRepository.findByActiveTrueAndPeriod(period)) {
            int current = computeQuestCurrent(q, todayTask, todayJournal, tasksThisWeek, journalsThisWeek, streak);
            boolean completed = current >= q.getTargetCount();
            boolean claimed = questClaimRepository.findByUserAccountAndQuestAndPeriodKey(currentUser, q, periodKey).isPresent();
            views.add(new QuestProgressView(q, current, completed, claimed));
        }
        return views;
    }

    private int computeQuestCurrent(Quest quest, DailyTask todayTask, DailyJournal todayJournal,
            List<DailyTask> tasksThisWeek, List<DailyJournal> journalsThisWeek, int streak) {
        boolean daily = quest.getPeriod() == Quest.Period.DAILY;
        return switch (quest.getConditionType()) {
            case TASK_COMPLETE_COUNT -> daily
                    ? (todayTask == null ? 0 : countDone(todayTask))
                    : tasksThisWeek.stream().mapToInt(this::countDone).sum();
            case JOURNAL_COUNT -> daily
                    ? (todayJournal != null ? 1 : 0)
                    : journalsThisWeek.size();
            case MOOD_LOG_COUNT -> daily
                    ? (todayJournal != null && todayJournal.getMoodScore() >= 1 ? 1 : 0)
                    : (int) journalsThisWeek.stream().filter(j -> j.getMoodScore() >= 1).count();
            case LOGIN_STREAK -> streak;
        };
    }

    // ★過去7日間の実際のEXP獲得量を再構成する（以前はMath.random()の偽データだった）
    private List<Integer> computeLast7DaysExp(UserAccount user, LocalDate today,
            List<DailyTask> myTasks, List<DailyJournal> myJournals, List<LoginRecord> myLoginRecords) {
        LocalDate start = today.minusDays(6);
        Map<LocalDate, Integer> expByDate = new HashMap<>();

        for (DailyTask t : myTasks) {
            if (t.getDate() == null || t.getDate().isBefore(start) || t.getDate().isAfter(today)) continue;
            int exp = 0;
            if (t.getTask1() != null && !t.getTask1().isBlank() && t.isTask1Done()) exp += 1;
            if (t.getTask2() != null && !t.getTask2().isBlank() && t.isTask2Done()) exp += 1;
            if (t.getTask3() != null && !t.getTask3().isBlank() && t.isTask3Done()) exp += 1;
            if (t.getBadHabit() != null && !t.getBadHabit().isBlank() && t.isBadHabitDone()) exp += 1;
            expByDate.merge(t.getDate(), exp, Integer::sum);
        }
        for (DailyJournal j : myJournals) {
            if (j.getDate() == null || j.getDate().isBefore(start) || j.getDate().isAfter(today)) continue;
            int exp = 0;
            if (j.getAchievement() != null && !j.getAchievement().isBlank()) exp += 1;
            if (j.getGratitude1() != null && !j.getGratitude1().isBlank()) exp += 1;
            if (j.getGratitude2() != null && !j.getGratitude2().isBlank()) exp += 1;
            if (j.getGratitude3() != null && !j.getGratitude3().isBlank()) exp += 1;
            if (j.getDiaryText() != null && !j.getDiaryText().isBlank()) exp += 1;
            if (j.getMoodScore() >= 1 && j.getMoodScore() <= 5) exp += 1;
            expByDate.merge(j.getDate(), exp, Integer::sum);
        }
        for (LoginRecord r : myLoginRecords) {
            if (r.isFrozen()) continue; // フリーズで補填された日はログインボーナスなし
            if (r.getLoginDate() == null || r.getLoginDate().isBefore(start) || r.getLoginDate().isAfter(today)) continue;
            expByDate.merge(r.getLoginDate(), LOGIN_BONUS_EXP, Integer::sum);
        }
        // クエスト報酬（periodKeyから日付を推定：デイリーはその日、ウィークリーはその週の月曜日に計上）
        for (QuestClaim c : questClaimRepository.findByUserAccount(user)) {
            LocalDate d;
            try {
                d = LocalDate.parse(c.getPeriodKey());
            } catch (Exception e) {
                continue;
            }
            if (d.isBefore(start) || d.isAfter(today)) continue;
            expByDate.merge(d, c.getQuest().getRewardExp(), Integer::sum);
        }

        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            result.add(expByDate.getOrDefault(start.plusDays(i), 0));
        }
        return result;
    }

    // ★直近14日間の気分スコア推移（記録がない日はnull＝グラフ上の空白になる）
    private List<Integer> computeMoodTrend(List<DailyJournal> myJournals, LocalDate today) {
        Map<LocalDate, Integer> moodByDate = new HashMap<>();
        for (DailyJournal j : myJournals) {
            if (j.getMoodScore() >= 1 && j.getMoodScore() <= 5) {
                moodByDate.put(j.getDate(), j.getMoodScore());
            }
        }
        LocalDate start = today.minusDays(13);
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            result.add(moodByDate.get(start.plusDays(i)));
        }
        return result;
    }
}
