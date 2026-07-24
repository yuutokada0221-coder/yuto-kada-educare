import java.sql.SQLException;
import java.util.List;
import java.util.Random;

public class Quiz {
    private WordManager wordManager;
    private int score;
    private int totalQuestions;

    public Quiz(WordManager wordManager) {
        this.wordManager = wordManager;
    }

    public boolean checkAnswer(Word word, String answer) {
        boolean isCorrect = word.getJapanese().equals(answer);
        if (isCorrect) {
            score++;
        }
        return isCorrect;
    }

    public Word getRandomWord() throws SQLException {
        List<Word> words = wordManager.getWords();
        if (words.isEmpty()) {
            return null;
        }
        Random random = new Random();
        return words.get(random.nextInt(words.size()));
    }

    public int getScore() {
        return score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public void resetScore() {
        this.score = 0;
        this.totalQuestions = 0;
    }
}
