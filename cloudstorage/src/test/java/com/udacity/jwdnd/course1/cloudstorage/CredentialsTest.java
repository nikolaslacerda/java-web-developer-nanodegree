package com.udacity.jwdnd.course1.cloudstorage;

import com.udacity.jwdnd.course1.cloudstorage.page.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CredentialsTest {

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

        driver.get(baseUrl + "/signup");
        SignupPage signupPage = new SignupPage(driver);
        signupPage.signup("John", "Doe", "user", "password");

        driver.get(baseUrl + "/login");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("user", "password");
    }

    @AfterEach
    public void afterEach() {
        if (this.driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    void testAddCredential() {
        driver.get(baseUrl + "/home");

        WebDriverWait wait = new WebDriverWait(driver, 1000);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));

        HomePage homePage = new HomePage(driver);
        homePage.openCredentialsTab();

        String url = "add url";
        String username = "add username";
        String decryptedPassword = "add decrypted password";

        CredentialsTab credentialsTab = new CredentialsTab(driver);
        credentialsTab.addCredential(url, username, decryptedPassword);

        Assertions.assertEquals(baseUrl + "/credentials/credential-save", driver.getCurrentUrl());

        ResultPage resultPage = new ResultPage(driver);
        Assertions.assertTrue(resultPage.isSuccessful());

        resultPage.clickToReturnHomeSuccess();

        driver.get(baseUrl + "/home");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));

        homePage.openCredentialsTab();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialTable")));

        WebElement credentialRow = credentialsTab.getCredentialRow(url, username);
        assertNotNull(credentialRow);

        assertNotEquals(decryptedPassword, credentialsTab.getCredentialPassword(credentialRow));
    }

    @Test
    @Order(2)
    void testViewablePasswordIsUnencrypted() {
        driver.get(baseUrl + "/home");

        WebDriverWait wait = new WebDriverWait(driver, 1000);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));

        HomePage homePage = new HomePage(driver);
        homePage.openCredentialsTab();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialTable")));

        String url = "add url";
        String username = "add username";
        String decryptedPassword = "add decrypted password";

        CredentialsTab credentialsTab = new CredentialsTab(driver);
        String editablePassword = credentialsTab.openEditCredentialDialog(url, username);

        assertEquals(decryptedPassword, editablePassword);
    }

    @Test
    @Order(3)
    void testEditCredential() {
        driver.get(baseUrl + "/home");

        WebDriverWait wait = new WebDriverWait(driver, 1000);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));

        HomePage homePage = new HomePage(driver);
        homePage.openCredentialsTab();

        String oldUrl = "add url";
        String oldUsername = "add username";

        String newUrl = "new edit url";
        String newUsername = "new edit username";
        String newDecryptedPassword = "new edit decrypted password";

        CredentialsTab credentialsTab = new CredentialsTab(driver);

        credentialsTab.editCredential(oldUrl, oldUsername, newUrl, newUsername, newDecryptedPassword);

        Assertions.assertEquals(baseUrl + "/credentials/credential-save", driver.getCurrentUrl());

        ResultPage resultPage = new ResultPage(driver);
        Assertions.assertTrue(resultPage.isSuccessful());

        resultPage.clickToReturnHomeSuccess();

        driver.get(baseUrl + "/home");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));

        homePage.openCredentialsTab();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialTable")));

        WebElement credentialRow = credentialsTab.getCredentialRow(newUrl, newUsername);

        assertNotNull(credentialRow);

        String editablePassword = credentialsTab.openEditCredentialDialog(newUrl, newUsername);

        assertEquals(newDecryptedPassword, editablePassword);
    }

    @Test
    @Order(4)
    void testDeleteCredential() {
        driver.get(baseUrl + "/home");

        WebDriverWait wait = new WebDriverWait(driver, 1000);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));

        HomePage homePage = new HomePage(driver);
        homePage.openCredentialsTab();

        String url = "new edit url";
        String username = "new edit username";

        CredentialsTab credentialsTab = new CredentialsTab(driver);
        credentialsTab.deleteCredential(url, username);

        ResultPage resultPage = new ResultPage(driver);
        Assertions.assertTrue(resultPage.isSuccessful());

        resultPage.clickToReturnHomeSuccess();

        driver.get(baseUrl + "/home");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));

        homePage.openCredentialsTab();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialTable")));

        assertNull(credentialsTab.getCredentialRow(url, username));
    }
}
