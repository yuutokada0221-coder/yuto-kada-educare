import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SearchProduct {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/educure_db";
        String user = "your_username";
        String password = "your_password";

        String query = "SELECT product_name, price FROM products WHERE price >= ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, 100000);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String productName = rs.getString("product_name");
                    int price = rs.getInt("price");
                    System.out.println("商品名: " + productName + ", 価格: " + price);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
