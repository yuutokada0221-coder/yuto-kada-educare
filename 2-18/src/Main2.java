public class Main2 {
    public static void main(String[] args) {
        int score = 85;
        String result = judgeGrade(score);
        System.out.println("成績: " + result);
    }

    public static String judgeGrade(int score) {
        if (score >= 80) {
            return "優";
        } else if (score >= 70) {
            return "良";
        } else if (score >= 60) {
            return "可";
        } else {
            return "不可";
        }
    }
}
