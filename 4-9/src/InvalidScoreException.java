public class InvalidScoreException extends GameScoreException {

    public InvalidScoreException(int score) {
        super("Score cannot be negative", score);
    }
}
