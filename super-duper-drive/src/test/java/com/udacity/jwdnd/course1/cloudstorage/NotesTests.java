package com.udacity.jwdnd.course1.cloudstorage;

import com.udacity.jwdnd.course1.cloudstorage.page.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NotesTests {

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
    void testNoteCreate() {

        driver.get(baseUrl + "/home");

        WebDriverWait wait = new WebDriverWait(driver, 1000);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));

        HomePage homePage = new HomePage(driver);
        homePage.openNotesTab();

        NotesTab notesTab = new NotesTab(driver);

        notesTab.addNote("New Note", "This is a note");
        Assertions.assertEquals(baseUrl + "/notes/note-save", driver.getCurrentUrl());

        ResultPage resultPage = new ResultPage(driver);
        Assertions.assertTrue(resultPage.isSuccessful());
        resultPage.clickToReturnHomeSuccess();

        driver.get(baseUrl + "/home");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));

        homePage.openNotesTab();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userTable")));

        assertNotNull(notesTab.getNoteRow("New Note", "This is a note"));
    }

    @Test
    @Order(2)
    void testNoteUpdate() {

        driver.get(baseUrl + "/home");

        WebDriverWait wait = new WebDriverWait(driver, 1000);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));

        HomePage homePage = new HomePage(driver);
        homePage.openNotesTab();

        NotesTab notesTab = new NotesTab(driver);
        notesTab.editNote("New Note", "This is a note", "Changed Note", "This is a changed note");

        ResultPage resultPage = new ResultPage(driver);
        resultPage.clickToReturnHomeSuccess();

        driver.get(baseUrl + "/home");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));
        homePage.openNotesTab();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userTable")));

        assertNotNull(notesTab.getNoteRow("Changed Note", "This is a changed note"));
    }

    @Test
    @Order(3)
    void testNoteDelete() {

        driver.get(baseUrl + "/home");

        WebDriverWait wait = new WebDriverWait(driver, 1000);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));

        HomePage homePage = new HomePage(driver);
        homePage.openNotesTab();

        NotesTab notesTab = new NotesTab(driver);
        notesTab.deleteNote("Changed Note", "This is a changed note");

        ResultPage resultPage = new ResultPage(driver);
        resultPage.clickToReturnHomeSuccess();

        driver.get(baseUrl + "/home");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));
        homePage.openNotesTab();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userTable")));

        assertNull(notesTab.getNoteRow("Changed Note", "This is a changed note"));
    }
}

