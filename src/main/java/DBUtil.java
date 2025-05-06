import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    private static final String URL = "jdbc:postgresql://localhost:5432/hbsdb";
    private static final String USER = "hbsadm";
    private static final String PASSWORD = "mdasbh!";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("🚨 Database connection failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
