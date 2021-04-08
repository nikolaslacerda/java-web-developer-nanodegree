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

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FilesTest {

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
    void testAddFile() {
        driver.get(baseUrl + "/home");

        WebDriverWait wait = new WebDriverWait(driver, 1000);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));

        HomePage homePage = new HomePage(driver);
        homePage.openFilesTab();

        FilesTab filesTab = new FilesTab(driver);
        File file = new File("./src/test/resources/cat.jpg");
        filesTab.uploadFile(file.getAbsolutePath());

        Assertions.assertEquals(baseUrl + "/files/file-upload", driver.getCurrentUrl());

        ResultPage resultPage = new ResultPage(driver);
        Assertions.assertTrue(resultPage.isSuccessful());

        resultPage.clickToReturnHomeSuccess();

        driver.get(baseUrl + "/home");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));

        homePage.openFilesTab();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("filesTable")));

        WebElement fileRow = filesTab.getFileRow("cat.jpg");
        assertNotNull(fileRow);
    }

    @Test
    @Order(2)
    void testViewFile() {
        driver.get(baseUrl + "/home");
        WebDriverWait wait = new WebDriverWait(driver, 1000);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));

        HomePage homePage = new HomePage(driver);
        homePage.openFilesTab();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("viewFileBtn"))).click();

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        File getDownloadedFile = getLatestFileFromDir("C:/Users/Nikolas.Santos/Downloads");

        String fileName = null;
        if (getDownloadedFile != null) {
            fileName = getDownloadedFile.getName();
        }
        Assertions.assertEquals("cat.jpg", fileName);
    }

    @Test
    @Order(3)
    void testDeleteFile() {
        driver.get(baseUrl + "/home");

        WebDriverWait wait = new WebDriverWait(driver, 1000);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));

        HomePage homePage = new HomePage(driver);
        homePage.openFilesTab();

        FilesTab filesTab = new FilesTab(driver);
        filesTab.deleteFile("cat.jpg");

        ResultPage resultPage = new ResultPage(driver);
        Assertions.assertTrue(resultPage.isSuccessful());

        resultPage.clickToReturnHomeSuccess();

        driver.get(baseUrl + "/home");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));

        homePage.openFilesTab();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("filesTable")));

        WebElement fileRow = filesTab.getFileRow("cat.jpg");
        assertNull(fileRow);
    }

    private File getLatestFileFromDir(String dirPath) {
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }

        File lastModifiedFile = files[0];
        for (int i = 1; i < files.length; i++) {
            if (lastModifiedFile.lastModified() < files[i].lastModified()) {
                lastModifiedFile = files[i];
            }
        }
        return lastModifiedFile;
    }
}
