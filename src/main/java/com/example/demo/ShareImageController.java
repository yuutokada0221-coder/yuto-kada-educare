package com.example.demo;

import com.example.demo.entity.UserAccount;
import com.example.demo.entity.LoginRecord;
import com.example.demo.repository.LoginRecordRepository;
import com.example.demo.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;

// ★連続記録のシェア画像をサーバー側で生成する（SNS投稿用のOG画像的なサイズ・見た目）。
// アプリのダークファンタジー配色（ネイビー×ゴールド）に合わせている。
@Controller
public class ShareImageController {

    @Autowired private UserAccountRepository userRepository;
    @Autowired private LoginRecordRepository loginRecordRepository;

    private static final int WIDTH = 1200;
    private static final int HEIGHT = 630;

    @GetMapping("/share-image")
    public ResponseEntity<byte[]> shareImage(Principal principal) throws IOException {
        UserAccount user = userRepository.findByUsername(principal.getName());
        LocalDate today = LocalDate.now();
        int streak = calculateStreak(user, today);
        int level = LevelingUtil.levelOf(user.getExp());
        int cumulativeDays = loginRecordRepository.findByUserAccount(user).size();

        byte[] png = renderImage(user.getUsername(), level, streak, cumulativeDays);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentDisposition(
                org.springframework.http.ContentDisposition.attachment()
                        .filename("streak-" + user.getUsername() + ".png")
                        .build());
        return new ResponseEntity<>(png, headers, org.springframework.http.HttpStatus.OK);
    }

    private byte[] renderImage(String username, int level, int streak, int cumulativeDays) throws IOException {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 背景：ダークネイビーの縦グラデーション（アプリのダークファンタジー基調に合わせる）
        GradientPaint bg = new GradientPaint(0, 0, new Color(0x0D, 0x14, 0x20), 0, HEIGHT, new Color(0x1D, 0x28, 0x39));
        g.setPaint(bg);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // ゴールドの縁取りカード
        Color gold = new Color(0xD9, 0xB4, 0x4A);
        g.setColor(gold);
        g.setStroke(new BasicStroke(6));
        g.draw(new RoundRectangle2D.Double(40, 40, WIDTH - 80, HEIGHT - 80, 32, 32));

        // タイトル
        g.setColor(gold);
        g.setFont(new Font("SansSerif", Font.BOLD, 40));
        drawCentered(g, "目標達成RPG", WIDTH / 2, 130);

        // ユーザー名
        g.setColor(new Color(0xEF, 0xE8, 0xD6));
        g.setFont(new Font("SansSerif", Font.PLAIN, 32));
        drawCentered(g, username, WIDTH / 2, 190);

        // 連続ログイン日数（メインの数字）
        g.setColor(gold);
        g.setFont(new Font("SansSerif", Font.BOLD, 160));
        drawCentered(g, String.valueOf(streak), WIDTH / 2, 400);

        g.setColor(new Color(0xC9, 0xCF, 0xDC));
        g.setFont(new Font("SansSerif", Font.BOLD, 36));
        drawCentered(g, "DAYS STREAK", WIDTH / 2, 450);

        // 下段：レベル・累計日数
        g.setFont(new Font("SansSerif", Font.PLAIN, 30));
        g.setColor(new Color(0xC9, 0xCF, 0xDC));
        drawCentered(g, "Lv. " + level + "　　累計達成日数 " + cumulativeDays + "日", WIDTH / 2, 540);

        g.dispose();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "png", out);
        return out.toByteArray();
    }

    private void drawCentered(Graphics2D g, String text, int centerX, int y) {
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        g.drawString(text, centerX - textWidth / 2, y);
    }

    // HomeControllerのcalculateStreakと同じロジック（private同士で共有できないため複製）
    private int calculateStreak(UserAccount user, LocalDate today) {
        int streak = 0;
        LocalDate checkDate = today;
        while (loginRecordRepository.existsByUserAccountAndLoginDate(user, checkDate)) {
            streak++;
            checkDate = checkDate.minusDays(1);
        }
        return streak;
    }
}
