public class Main3 {
    public static void main(String[] args) {

        ScoreManager manager = new ScoreManager();

        int[] scores = {50, -10, 150};

        for (int score : scores) {
            System.out.println("得点: " + score);

            try {
                manager.validateScore(score);
            } catch (GameScoreException e) {
                System.out.println("エラー: " + e.getMessage() + " (" + e.getScore() + ")");
            }

            System.out.println();
        }
    }
}
