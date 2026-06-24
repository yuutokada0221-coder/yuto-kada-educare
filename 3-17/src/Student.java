public class Student {
    private String name;
    private int score;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        if (name == null || name.length() < 1 || name.length() > 20) {
            System.out.println("エラー： 名前は1-20文字で設定してください");
            return;
        }
        this.name = name;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        if (score < 0 || score > 100) {
            System.out.println("エラー： 点数は0-100の範囲で設定してください");
            return;
        }
        this.score = score;
    }
}