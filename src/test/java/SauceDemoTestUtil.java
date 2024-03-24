import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Browser;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class SauceDemoTestUtil {

    protected static WebDriver driver;

    protected static Connection connection;

    protected ArrayList<String> validUserNames = new ArrayList<>();

    protected static void setUpRemoteWebDriver() throws MalformedURLException {
        DesiredCapabilities dc = new DesiredCapabilities();
        dc.setBrowserName(Browser.CHROME.browserName());
        driver = new RemoteWebDriver(new URL("http://localhost:4444"), dc);
    }

    protected static void setUpJdBcConnection(String jdbcUrl) {
        connection = Optional.ofNullable(ConnectionFactory.getConnection(jdbcUrl))
                .orElseThrow(() -> new RuntimeException("Connection was not established to the Mysql database"));
    }


    protected void saveUserNames(String[] userNames) throws SQLException {

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from credentials;");

        if (resultSet.next()) {
            System.out.println("Records already exists! Skipping insertion of valid usernames.");
        } else {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO credentials VALUES(?)");
            Arrays.stream(userNames).forEach(userName -> {
                try {
                    preparedStatement.setString(1, userName);
                    preparedStatement.addBatch();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            int[] recordsInserted = preparedStatement.executeBatch();
            System.out.println("Records inserted: " + recordsInserted.length);
            connection.commit();
        }
    }

    protected ArrayList<String> getSavedUserNames() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet userNameRS = statement.executeQuery("select username from credentials");
        while (userNameRS.next()) {
            String userName = userNameRS.getString(1);
            validUserNames.add(userName);
        }
        return validUserNames;
    }
}



