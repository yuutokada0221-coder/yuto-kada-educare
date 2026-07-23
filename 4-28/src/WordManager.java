import java.util.ArrayList;
import java.util.List;

public class WordManager {
    private List<Word> words;

    public WordManager() {
        this.words = new ArrayList<>();
    }

    public void addWord(Word word) {
        words.add(word);
    }

    public List<Word> getWords() {
        return words;
    }

    public int getWordCount() {
        return words.size();
    }
}
