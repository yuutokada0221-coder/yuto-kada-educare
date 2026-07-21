package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

// ★メール送信の窓口。SMTP設定が未設定/誤りでも、送信失敗でアプリ全体が落ちないよう
// 例外はここで握りつぶしてログに残すだけにする（呼び出し側はUXを止めない）。
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("【目標達成RPG】パスワード再設定のご案内");
        message.setText(
                "パスワード再設定のリクエストを受け付けました。\n\n" +
                "以下のリンクから新しいパスワードを設定してください（30分間有効）。\n" +
                resetLink + "\n\n" +
                "心当たりがない場合は、このメールを無視してください。"
        );
        try {
            mailSender.send(message);
        } catch (MailException e) {
            log.error("パスワードリセットメールの送信に失敗しました（SMTP設定を確認してください）: {}", e.getMessage());
        }
    }
}
