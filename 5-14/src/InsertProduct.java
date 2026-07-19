import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertProduct {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/educure_db";
        String user = "your_username";
        String password = "your_password";

        String insertSQL = "INSERT INTO products (product_name, price, stock) VALUES (?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            
            pstmt.setString(1, "スマートフォン");
            pstmt.setInt(2, 80000);
            pstmt.setInt(3, 30);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("商品が正常に追加されました。");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
