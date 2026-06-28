public abstract class GameScoreException extends Exception {
    private int score;

    public GameScoreException(String message, int score) {
        super(message);
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}
