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

public class FilesTab {

    @FindBy(id = "uploadFileButton")
    private WebElement uploadFileButton;

    @FindBy(id = "file-row")
    private List<WebElement> fileElements;

    @FindBy(id = "fileUpload")
    private WebElement chooseFile;

    @FindBy(id = "deleteFileButton")
    private WebElement deleteFileButton;

    private final WebDriver driver;


    public FilesTab(WebDriver driver) {
        PageFactory.initElements(driver, this);
        this.driver = driver;
    }

    public void uploadFile(String filePath) {
        this.chooseFile.sendKeys(filePath);
        this.uploadFileButton.click();
    }

    public void deleteFile(String fileName) {
        WebElement fileRow = getFileRow(fileName);
        if (fileRow == null) return;

        fileRow.findElement(By.id("deleteFileButton")).click();

        try {
            WebDriverWait wait = new WebDriverWait(driver, 1);
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            alert.accept();
        } catch (Throwable e) {
            System.err.println("Error came while waiting for the alert popup. " + e.getMessage());
        }
    }

    public WebElement getFileRow(String fileName) {
        for (WebElement fileElement : fileElements) {
            WebElement fileNameElement = fileElement.findElement(By.id("file-name"));

            if (fileNameElement.getText().equals(fileName)) return fileElement;
        }
        return null;
    }
}
