package com.udacity.jwdnd.course1.cloudstorage.page;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class CredentialsTab {

    @FindBy(id = "addNewCredentialBtn")
    private WebElement addNewCredentialBtn;

    @FindBy(className = "credential-row")
    private List<WebElement> credentialElements;

    @FindBy(id = "credential-url")
    private WebElement credentialUrlField;

    @FindBy(id = "credential-username")
    private WebElement credentialUsernameField;

    @FindBy(id = "credential-password")
    private WebElement credentialPasswordField;

    @FindBy(id = "credentialSaveBtn")
    private WebElement credentialSaveBtn;

    private final WebDriver driver;

    public CredentialsTab(WebDriver driver) {
        PageFactory.initElements(driver, this);
        this.driver = driver;
    }

    public void addCredential(String url, String username, String decryptedPassword) {
        new WebDriverWait(driver, 1).until(ExpectedConditions.elementToBeClickable(addNewCredentialBtn)).click();
        new WebDriverWait(driver, 1).until(ExpectedConditions.elementToBeClickable(credentialSaveBtn));

        credentialUrlField.sendKeys(url);
        credentialUsernameField.sendKeys(username);
        credentialPasswordField.sendKeys(decryptedPassword);
        credentialSaveBtn.click();
    }

    public String openEditCredentialDialog(String url, String username) {
        WebElement credentialRow = getCredentialRow(url, username);

        if (credentialRow == null)
            return null;

        credentialRow.findElement(By.className("credential-edit")).click();

        new WebDriverWait(driver, 1).until(ExpectedConditions.elementToBeClickable(credentialSaveBtn));

        new WebDriverWait(driver, 1);
        return credentialPasswordField.getAttribute("value");
    }

    public void editCredential(String oldUrl, String oldUsername, String newUrl, String newUsername, String newDecryptedPassword) {
        WebElement credentialRow = getCredentialRow(oldUrl, oldUsername);

        if (credentialRow == null)
            return;

        credentialRow.findElement(By.id("credentialUpdateBtn")).click();

        new WebDriverWait(driver, 1).until(ExpectedConditions.elementToBeClickable(credentialSaveBtn));

        credentialUrlField.clear();
        credentialUsernameField.clear();
        credentialPasswordField.clear();

        credentialUrlField.sendKeys(newUrl);
        credentialUsernameField.sendKeys(newUsername);
        credentialPasswordField.sendKeys(newDecryptedPassword);
        credentialSaveBtn.click();

    }

    public void deleteCredential(String url, String username) {
        WebElement credentialRow = getCredentialRow(url, username);
        if (credentialRow == null) return;

        credentialRow.findElement(By.id("credentialDeleteBtn")).click();

        try {
            WebDriverWait wait = new WebDriverWait(driver, 1);
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());

            alert.accept();
        } catch (Throwable e) {
            System.err.println("Error came while waiting for the alert popup. " + e.getMessage());
        }
    }

    public WebElement getCredentialRow(String url, String username) {
        for (WebElement credentialElemnt : credentialElements) {
            WebElement urlElement = credentialElemnt.findElement(By.className("credential-url"));
            WebElement usernameElement = credentialElemnt.findElement(By.className("credential-username"));

            if (urlElement.getText().equals(url) && usernameElement.getText().equals(username)) return credentialElemnt;
        }
        return null;
    }

    public String getCredentialPassword(WebElement credentialElement) {
        WebElement passwordElement = credentialElement.findElement(By.className("credential-password"));
        if (passwordElement != null)
            return passwordElement.getText();
        return null;
    }
}
