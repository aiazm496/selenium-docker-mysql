import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    public static Connection getConnection(String url) {
        Connection connection;

        try {
            connection = DriverManager.getConnection(url, "root", "root");
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            return null;
        }
        return connection;
    }
}
