import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateProduct {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/educure_db";
        String user = "your_username";
        String password = "your_password";

        String updatePriceSQL = "UPDATE products SET price = price - 5000 WHERE product_name = 'タブレット'";
        String updateStockSQL = "UPDATE products SET stock = stock + 5 WHERE price < 50000";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement priceStmt = conn.prepareStatement(updatePriceSQL);
             PreparedStatement stockStmt = conn.prepareStatement(updateStockSQL)) {

            int priceRows = priceStmt.executeUpdate();
            System.out.println("価格を値下げした行数: " + priceRows);

            int stockRows = stockStmt.executeUpdate();
            System.out.println("在庫を増やした行数: " + stockRows);

        } catch (SQLException e) {
            System.out.println("データベース処理中にエラーが発生しました。");
            e.printStackTrace();
        }
    }
}
