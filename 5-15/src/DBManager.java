import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {
    private Connection connection;
    // データベース接続情報
    private final String URL = "jdbc:postgresql://localhost:5432/vocabulary_db";
    private final String USER = "postgres";
    private final String PASSWORD = "CYV94XpcfV";

    public DBManager() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            // 接続エラー時の適切なエラーハンドリング
            System.out.println("エラー：データベース接続に失敗しました。接続設定やデータベースの起動状態を確認してください。");
            System.exit(1); // プログラムを安全に終了
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

