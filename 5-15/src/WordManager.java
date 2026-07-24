import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WordManager {
    private DBManager dbManager;

    public WordManager(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    public void addWord(Word word) {
        String sql = "INSERT INTO words (english, japanese) VALUES (?, ?)";
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, word.getEnglish());
            pstmt.setString(2, word.getJapanese());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Word> getWords() {
        List<Word> words = new ArrayList<>();
        String sql = "SELECT english, japanese FROM words";
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                words.add(new Word(rs.getString("english"), rs.getString("japanese")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return words;
    }

    public int getWordCount() {
        String sql = "SELECT COUNT(*) FROM words";
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public void deleteWord(String english) {
        String sql = "DELETE FROM words WHERE english = ?";
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, english);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateWord(String english, String newJapanese) {
        String sql = "UPDATE words SET japanese = ? WHERE english = ?";
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, newJapanese);
            pstmt.setString(2, english);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

