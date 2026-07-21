package com.example.demo;

// ★パスワード強度のサーバー側検証。register/reset-passwordの両方から使う共通ルール。
// クライアント側のminlength属性はブラウザ開発者ツール等で簡単に外せるため、サーバー側でも必ず確認する。
public final class PasswordPolicy {

    private static final int MIN_LENGTH = 8;

    private PasswordPolicy() {}

    /** 満たしていれば null、満たしていなければユーザーに見せるエラーメッセージを返す */
    public static String validate(String password) {
        if (password == null || password.length() < MIN_LENGTH) {
            return "パスワードは" + MIN_LENGTH + "文字以上で設定してください。";
        }
        boolean hasLetter = password.chars().anyMatch(Character::isLetter);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        if (!hasLetter || !hasDigit) {
            return "パスワードは英字と数字の両方を含めてください。";
        }
        return null;
    }
}
