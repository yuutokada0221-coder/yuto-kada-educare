import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProductMaintenance {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/educure_db";
        String username = "your_username";
        String password = "your_password";

        String updatePriceSQL = "UPDATE products SET price = 0 WHERE stock = 0";
        String deleteSQL = "DELETE FROM products WHERE price >= 200000";
        String updateStockSQL = "UPDATE products SET stock = 20 WHERE price >= 100000 AND stock <= 10";

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            
            conn.setAutoCommit(false);

            try (PreparedStatement priceStmt = conn.prepareStatement(updatePriceSQL);
                 PreparedStatement deleteStmt = conn.prepareStatement(deleteSQL);
                 PreparedStatement stockStmt = conn.prepareStatement(updateStockSQL)) {

                int priceRows = priceStmt.executeUpdate();
                System.out.println("影響を受けた行数（在庫数0の商品価格を0に設定）: " + priceRows);

                int deleteRows = deleteStmt.executeUpdate();
                System.out.println("影響を受けた行数（価格が200000以上の商品削除）: " + deleteRows);

                int stockRows = stockStmt.executeUpdate();
                System.out.println("影響を受けた行数（価格100000以上、在庫数10以下の商品を在庫数20に更新）: " + stockRows);

                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
