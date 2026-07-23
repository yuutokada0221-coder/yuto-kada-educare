import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateStock {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/educure_db";
        String user = "your_username";
        String password = "your_password";

        String checkStockSQL = "SELECT COUNT(*) FROM products WHERE stock > 0";
        String updateSQL = "UPDATE products SET stock = CASE WHEN stock >= 10 THEN stock - 10 ELSE 0 END";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement checkStmt = conn.prepareStatement(checkStockSQL);
             ResultSet rs = checkStmt.executeQuery()) {

            if (rs.next()) {
                int count = rs.getInt(1);
                
                if (count == 0) {
                    System.out.println("エラー: 在庫を更新できる商品がありません。");
                } else {
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {
                        int rowsAffected = updateStmt.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("在庫が正常に更新されました。");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("データベース処理中にエラーが発生しました。");
            e.printStackTrace();
        }
    }
}
