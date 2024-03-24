import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.sql.SQLException;

public class SauceDemoLoginTest extends SauceDemoTestUtil {

    private final static String JDBC_URL = "jdbc:mysql://localhost:3306/test";
    private static final String PASSWORD = "secret_sauce";
    @BeforeTest
    public void setUp() throws MalformedURLException, SQLException {
        setUpRemoteWebDriver();
        setUpJdBcConnection(JDBC_URL);
        driver.get("https://www.saucedemo.com/");
        WebElement listOfUsernamesEl = driver.findElement(By.cssSelector("#login_credentials"));
        String unusedChar = "<h4>Accepted usernames are:</h4>";
        String[] userNames = listOfUsernamesEl.getAttribute("innerHTML").replace(unusedChar,"").split("<br>");
        //store the accepted usernames in the mysql database running in the docker container using jdbc
        saveUserNames(userNames);
    }

    @Test
    public void verifyLoginWithAcceptedUsernamesForSauceDemoWebsite() throws SQLException {
        validUserNames = getSavedUserNames();
        if (!validUserNames.isEmpty()) {
            validUserNames.forEach(validUserName -> {
                        driver.get("https://www.saucedemo.com/");
                        driver.findElement(By.cssSelector("#user-name")).sendKeys(validUserName);
                        driver.findElement(By.cssSelector("#password")).sendKeys(PASSWORD);
                        driver.findElement(By.cssSelector("#login-button")).click();
                    }
            );
        }else {
            throw new AssertionError("No valid usernames were found in the database");
        }
    }

    @AfterTest
    public void tearDown() throws SQLException {
        driver.quit();
        connection.close();
    }

}