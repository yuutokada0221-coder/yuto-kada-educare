public class Main3 {
    public static boolean validateUsername(String username) {
        if (!username.matches("^[a-zA-Z0-9_]{4,16}$")) {
            return false;
        }
        if (username.matches("^[0-9]+$")) {
            return false;
        }
        return true;
    }
    public static boolean validatePassword(String password) {
        if (password.length() < 8) {
            return false;
        }
        if (!password.matches(".*[A-Z].*")) {
            return false;
        }
        if (!password.matches(".*[0-9].*")) {
            return false;
        }
        return true;
    }
    public static void main(String[] args) {
        System.out.println("ユーザー名チェック:");
        String[] usernames = {"Player_1", "123456", "P@layer"};
        for (String u : usernames) {
            if (validateUsername(u)) {
                System.out.println(u + ": 有効");
            } else {
                System.out.println(u + ": 無効");
            }
        }
        System.out.println("パスワードチェック:");
        String[] passwords = {"Password123", "password123", "Pass"};
        for (String p : passwords) {
            if (validatePassword(p)) {
                System.out.println(p + ": 有効");
            } else {
                System.out.println(p + ": 無効");
            }
        }
    }
}
    
