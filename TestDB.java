import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestDB {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://dpg-d9bq3spkh4rs73bfonc8-a.singapore-postgres.render.com:5432/ecom_db_0vpa?sslmode=require";
        String user = "ecom_db_0vpa_user";
        String password = "252622";
        
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
