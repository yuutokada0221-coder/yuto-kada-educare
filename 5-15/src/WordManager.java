import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WordManager {
    private DBManager dbManager;

    public WordManager(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    public void addWord(Word word) throws SQLException {
        String sql = "INSERT INTO words (english, japanese) VALUES (?, ?)";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, word.getEnglish());
            stmt.setString(2, word.getJapanese());
            stmt.executeUpdate();
        }
    }

    public List<Word> getWords() throws SQLException {
        List<Word> words = new ArrayList<>();
        String sql = "SELECT english, japanese FROM words";
        try (Statement stmt = dbManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                words.add(new Word(rs.getString("english"), rs.getString("japanese")));
            }
        }
        return words;
    }

    public int getWordCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM words";
        try (Statement stmt = dbManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public void deleteWord(String english) throws SQLException {
        String sql = "DELETE FROM words WHERE english = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, english);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("指定された単語が見つかりませんでした。");
            }
        }
    }

    public void updateWord(String english, String newJapanese) throws SQLException {
        String sql = "UPDATE words SET japanese = ? WHERE english = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, newJapanese);
            stmt.setString(2, english);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("指定された単語が見つかりませんでした。");
            }
        }
    }
}
