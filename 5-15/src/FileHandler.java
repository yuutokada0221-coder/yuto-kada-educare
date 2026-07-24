import java.io.*;
import java.sql.SQLException;
import java.util.List;

public class FileHandler {
    
    public void exportToCSV(List<Word> words, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Word word : words) {
                writer.write(word.getEnglish() + "," + word.getJapanese());
                writer.newLine();
            }
        }
    }

    public int importFromCSV(String filename, WordManager wordManager) throws IOException, SQLException {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    Word word = new Word(parts[0].trim(), parts[1].trim());
                    wordManager.addWord(word);
                    count++;
                }
            }
        }
        return count;
    }
}
