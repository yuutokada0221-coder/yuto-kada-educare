import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Quiz {
    private WordManager wordManager;
    private int score;
    private int totalQuestions;
    private List<Word> quizWords;
    private int currentQuestionIndex;

    public Quiz(WordManager wordManager) {
        this.wordManager = wordManager;
        this.score = 0;
        this.totalQuestions = wordManager.getWordCount();
        
        // 登録されている単語リストをコピーしてシャッフル（ランダム出題用）
        this.quizWords = new ArrayList<>(wordManager.getWords());
        Collections.shuffle(this.quizWords);
        this.currentQuestionIndex = 0;
    }

    public Word getRandomWord() {
        if (currentQuestionIndex < quizWords.size()) {
            return quizWords.get(currentQuestionIndex++);
        }
        return null;
    }

    public boolean checkAnswer(Word word, String answer) {
        if (word.getJapanese().equals(answer)) {
            score++;
            return true;
        }
        return false;
    }

    public int getScore() {
        return score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }
}
