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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/")
    public String home(Model model, Principal principal,
                       @RequestParam(name = "loginSuccess", required = false) String loginSuccess,
                       @RequestParam(name = "error", required = false) String error,
                       @RequestParam(name = "notAdmin", required = false) String notAdmin,
                       @RequestParam(name = "damage", required = false) Integer damage,
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
        model.addAttribute("damageDealt", damage);
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

        // --- ★達成率：入力したTODOのうち実際に完了できた割合 ---
        int todoInputCount = 0;
        int todoCompletedCount = 0;
        for (DailyTask t : myTasks) {
            if (t.getTask1() != null && !t.getTask1().isBlank()) { todoInputCount++; if (t.isTask1Done()) todoCompletedCount++; }
            if (t.getTask2() != null && !t.getTask2().isBlank()) { todoInputCount++; if (t.isTask2Done()) todoCompletedCount++; }
            if (t.getTask3() != null && !t.getTask3().isBlank()) { todoInputCount++; if (t.isTask3Done()) todoCompletedCount++; }
        }
        int achievementRate = todoInputCount > 0 ? (int) Math.round(todoCompletedCount * 100.0 / todoInputCount) : 0;
        model.addAttribute("achievementRate", achievementRate);

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

        Optional<DailyTask> todayTaskOpt = taskRepository.findByUserAccountAndDate(currentUser, today);
        Optional<DailyJournal> todayJournalOpt = journalRepository.findByUserAccountAndDate(currentUser, today);
        model.addAttribute("isTaskDoneToday", todayTaskOpt.isPresent());
        model.addAttribute("isJournalDoneToday", todayJournalOpt.isPresent());

        // --- ★モンスター戦闘演出：今日のTODO達成状況をモンスターのHPに反映 ---
        addMonsterState(model, todayTaskOpt);

        // --- ★デイリー／ウィークリークエスト ---
        List<DailyTask> tasksThisWeek = myTasks.stream().filter(t -> !t.getDate().isBefore(mondayOfThisWeek)).collect(Collectors.toList());
        List<DailyJournal> journalsThisWeek = myJournals.stream().filter(j -> !j.getDate().isBefore(mondayOfThisWeek)).collect(Collectors.toList());
        DailyTask todayTask = todayTaskOpt.orElse(null);
        DailyJournal todayJournal = todayJournalOpt.orElse(null);

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

        DailyTask taskForm = new DailyTask();
        taskForm.setTask1(currentUser.getFixedHabit1());
        taskForm.setTask2(currentUser.getFixedHabit2());
        taskForm.setTask3(currentUser.getFixedHabit3());
        taskForm.setBadHabit(currentUser.getFixedBadHabit());
        model.addAttribute("taskForm", taskForm);

        model.addAttribute("journals", myJournals);
        model.addAttribute("tasks", myTasks);

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

        List<Map<String, Object>> leaderboard = new ArrayList<>();
        leaderboard.add(friendLeaderboardRow(currentUser, currentUser, today, true));
        for (Friendship f : accepted) {
            UserAccount friend = f.getRequester().getId().equals(currentUser.getId()) ? f.getAddressee() : f.getRequester();
            leaderboard.add(friendLeaderboardRow(friend, currentUser, today, false));
        }
        leaderboard.sort((a, b) -> {
            int levelCompare = Integer.compare((int) b.get("level"), (int) a.get("level"));
            if (levelCompare != 0) return levelCompare;
            return Integer.compare((int) b.get("exp"), (int) a.get("exp"));
        });
        model.addAttribute("friendLeaderboard", leaderboard);

        // ★今日自分を応援してくれた人の一覧（みんチャレ/Strava風のkudos表示）
        List<Cheer> cheersReceivedToday = cheerRepository.findByToUserAndCheerDate(currentUser, today);
        model.addAttribute("cheersReceivedToday", cheersReceivedToday);
    }

    private Map<String, Object> friendLeaderboardRow(UserAccount user, UserAccount currentUser, LocalDate today, boolean isSelf) {
        Map<String, Object> row = new HashMap<>();
        row.put("userId", user.getId());
        row.put("username", user.getUsername());
        row.put("level", levelOf(user.getExp()));
        row.put("exp", user.getExp());
        row.put("streak", calculateStreak(user, today));
        row.put("isSelf", isSelf);
        row.put("alreadyCheeredToday", !isSelf
                && cheerRepository.existsByFromUserAndToUserAndCheerDate(currentUser, user, today));
        row.put("cheersReceivedToday", cheerRepository.findByToUserAndCheerDate(user, today).size());
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

    @PostMapping("/saveTask")
    public String saveTask(@ModelAttribute DailyTask taskForm, Principal principal) {
        UserAccount currentUser = userRepository.findByUsername(principal.getName());
        LocalDate today = LocalDate.now();
        if (taskRepository.findByUserAccountAndDate(currentUser, today).isPresent()) return "redirect:/";

        int earnedExp = 0;
        if (taskForm.getTask1() != null && !taskForm.getTask1().trim().isEmpty() && taskForm.isTask1Done()) earnedExp += 1;
        if (taskForm.getTask2() != null && !taskForm.getTask2().trim().isEmpty() && taskForm.isTask2Done()) earnedExp += 1;
        if (taskForm.getTask3() != null && !taskForm.getTask3().trim().isEmpty() && taskForm.isTask3Done()) earnedExp += 1;
        if (taskForm.getBadHabit() != null && !taskForm.getBadHabit().trim().isEmpty() && taskForm.isBadHabitDone()) earnedExp += 1;

        if (earnedExp == 0) return "redirect:/?error=taskEmpty";

        // ★ボスへのダメージ＝達成したTODOの数（1つにつき1ダメージ）
        int damage = 0;
        if (taskForm.getTask1() != null && !taskForm.getTask1().trim().isEmpty() && taskForm.isTask1Done()) damage++;
        if (taskForm.getTask2() != null && !taskForm.getTask2().trim().isEmpty() && taskForm.isTask2Done()) damage++;
        if (taskForm.getTask3() != null && !taskForm.getTask3().trim().isEmpty() && taskForm.isTask3Done()) damage++;

        int levelBefore = levelOf(currentUser.getExp());
        currentUser.setExp(currentUser.getExp() + earnedExp);
        int levelAfter = levelOf(currentUser.getExp());
        userRepository.save(currentUser);
        taskForm.setDate(today);
        taskForm.setUserAccount(currentUser);
        taskRepository.save(taskForm);

        List<String> params = new ArrayList<>();
        if (damage > 0) params.add("damage=" + damage);
        if (levelAfter > levelBefore) params.add("levelUp=" + levelAfter);
        return params.isEmpty() ? "redirect:/" : "redirect:/?" + String.join("&", params);
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

    @PostMapping("/deleteJournal/{id}")
    public String deleteJournal(@PathVariable Long id, Principal principal) {
        UserAccount currentUser = userRepository.findByUsername(principal.getName());
        journalRepository.findById(id).ifPresent(journal -> {
            if (journal.getUserAccount() != null && journal.getUserAccount().getId().equals(currentUser.getId())) {
                if (journal.getPhotoFilename() != null) {
                    try {
                        Files.deleteIfExists(JOURNAL_PHOTO_DIR.resolve(journal.getPhotoFilename()));
                    } catch (IOException ignored) {
                        // 削除に失敗してもレコード自体の削除は続行する（孤立ファイルはディスク上に残るのみ）
                    }
                }
                journalRepository.deleteById(id);
            }
        });
        return "redirect:/";
    }

    @PostMapping("/deleteTask/{id}")
    public String deleteTask(@PathVariable Long id, Principal principal) {
        UserAccount currentUser = userRepository.findByUsername(principal.getName());
        taskRepository.findById(id).ifPresent(task -> {
            if (task.getUserAccount() != null && task.getUserAccount().getId().equals(currentUser.getId())) {
                taskRepository.deleteById(id);
            }
        });
        return "redirect:/";
    }

    // ============================ ここから内部ヘルパー ============================

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

    // 今日のTODO達成状況をモンスターのHPとして model に積む
    // ★ボスのHPは常に3固定。3つ全て達成したときだけ撃破できる（入力数に関わらない）
    private void addMonsterState(Model model, Optional<DailyTask> todayTaskOpt) {
        int monsterHpMax = 3;
        int done = 0;
        if (todayTaskOpt.isPresent()) {
            DailyTask t = todayTaskOpt.get();
            if (t.getTask1() != null && !t.getTask1().trim().isEmpty() && t.isTask1Done()) done++;
            if (t.getTask2() != null && !t.getTask2().trim().isEmpty() && t.isTask2Done()) done++;
            if (t.getTask3() != null && !t.getTask3().trim().isEmpty() && t.isTask3Done()) done++;
        }
        int monsterHpCurrent = Math.max(0, monsterHpMax - done);
        boolean monsterDefeated = done >= monsterHpMax;
        model.addAttribute("monsterHpMax", monsterHpMax);
        model.addAttribute("monsterHpCurrent", monsterHpCurrent);
        model.addAttribute("monsterHpPercent", (int) Math.round(monsterHpCurrent * 100.0 / monsterHpMax));
        model.addAttribute("monsterDefeated", monsterDefeated);
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
