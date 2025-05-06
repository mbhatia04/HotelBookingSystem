import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresConnectionTest {

    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "pewzapie";

        System.out.println("Trying to connect to PostgreSQL...");

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            if (connection != null) {
                System.out.println("🎉 Connection successful!");
            } else {
                System.out.println("😞 Connection failed.");
            }
        } catch (SQLException e) {
            System.err.println("🚨 Connection error:");
            e.printStackTrace();
        }
    }
}
