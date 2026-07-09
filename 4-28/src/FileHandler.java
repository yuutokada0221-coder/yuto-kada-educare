import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FileHandler {

    public int exportToCSV(List<Word> words, String filename) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (Word word : words) {
                bw.write(word.getEnglish() + "," + word.getJapanese());
                bw.newLine();
            }
            return words.size();
        }
    }

    public int importFromCSV(String filename, WordManager wordManager) throws IOException, IllegalArgumentException {
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                // カンマがない等の不正フォーマットチェック（テストケース3-3対応）
                if (parts.length != 2) {
                    throw new IllegalArgumentException("不正な形式のCSVファイルです。");
                }
                wordManager.addWord(new Word(parts[0], parts[1]));
                count++;
            }
        }
        return count;
    }
}
