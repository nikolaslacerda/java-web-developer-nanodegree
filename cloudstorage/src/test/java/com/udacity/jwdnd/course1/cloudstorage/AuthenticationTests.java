package com.udacity.jwdnd.course1.cloudstorage;

import com.udacity.jwdnd.course1.cloudstorage.page.HomePage;
import com.udacity.jwdnd.course1.cloudstorage.page.LoginPage;
import com.udacity.jwdnd.course1.cloudstorage.page.SignupPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationTests {

    @LocalServerPort
    private int port;

    private WebDriver driver;

    private String baseUrl;

    @BeforeAll
    static void beforeAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void beforeEach() {
        this.driver = new ChromeDriver();
        this.baseUrl = "http://localhost:" + this.port;
    }

    @AfterEach
    public void afterEach() {
        if (this.driver != null) {
            driver.quit();
        }
    }

    @Test
    void testAuthorizedAccess() {
        driver.get(baseUrl + "/login");
        assertEquals((baseUrl + "/login"), driver.getCurrentUrl());

        driver.get(baseUrl + "/signup");
        assertEquals((baseUrl + "/signup"), driver.getCurrentUrl());
    }

    @Test
    void testUnauthorizedAccess() {
        driver.get(baseUrl + "/home");
        assertNotEquals((baseUrl + "/home"), driver.getCurrentUrl());

        driver.get(baseUrl + "/notes");
        assertNotEquals((baseUrl + "/notes"), driver.getCurrentUrl());
    }

    @Test
    void testSignupLoginLogout() {
        driver.get(baseUrl + "/signup");

        SignupPage signupPage = new SignupPage(driver);
        signupPage.signup("John", "Doe", "user", "pass");

        driver.get(baseUrl + "/login");

        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("user", "pass");

        driver.get(baseUrl + "/home");

        String expectedUrl = baseUrl + "/home";
        assertEquals(expectedUrl, driver.getCurrentUrl());

        HomePage homePage = new HomePage(driver);
        homePage.logout();

        assertEquals(baseUrl + "/login", driver.getCurrentUrl());

        driver.get(baseUrl + "/home");

        assertEquals(baseUrl + "/login", driver.getCurrentUrl());
    }
}
