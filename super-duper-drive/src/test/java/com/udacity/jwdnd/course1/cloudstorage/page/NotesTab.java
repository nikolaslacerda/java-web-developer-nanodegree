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

public class NotesTab {

    private final WebDriver driver;

    @FindBy(id = "addNoteBtn")
    private WebElement addNoteBtn;

    @FindBy(id = "nav-notes-tab")
    private WebElement notesTab;

    @FindBy(className = "note-row")
    private List<WebElement> noteElements;

    @FindBy(id = "note-title")
    private WebElement noteTitleField;

    @FindBy(id = "note-description")
    private WebElement noteDescriptionField;

    @FindBy(id = "noteSaveBtn")
    private WebElement noteSaveBtn;

    public NotesTab(WebDriver driver) {
        PageFactory.initElements(driver, this);
        this.driver = driver;
    }

    public void addNote(String noteTitle, String noteDescription) {
        new WebDriverWait(driver, 1).until(ExpectedConditions.elementToBeClickable(addNoteBtn)).click();
        new WebDriverWait(driver, 1).until(ExpectedConditions.elementToBeClickable(noteSaveBtn));

        noteTitleField.clear();
        noteDescriptionField.clear();

        noteTitleField.sendKeys(noteTitle);
        noteDescriptionField.sendKeys(noteDescription);

        noteSaveBtn.click();
    }

    public void editNote(String oldTitle, String oldDescription, String newTitle, String newDescription) {
        WebElement noteRow = getNoteRow(oldTitle, oldDescription);

        if (noteRow == null)
            return;

        noteRow.findElement(By.id("editNoteBtn")).click();

        new WebDriverWait(driver, 500).until(ExpectedConditions.elementToBeClickable(noteSaveBtn));

        noteTitleField.clear();
        noteDescriptionField.clear();

        noteTitleField.sendKeys(newTitle);
        noteDescriptionField.sendKeys(newDescription);
        noteSaveBtn.click();
    }

    public void deleteNote(String title, String description) {
        WebElement noteRow = getNoteRow(title, description);

        if (noteRow == null) return;

        noteRow.findElement(By.id("deleteNoteBtn")).click();

        try {
            WebDriverWait wait = new WebDriverWait(driver, 1);
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            alert.accept();
        } catch (Throwable e) {
            System.err.println("Error came while waiting for the alert popup. " + e.getMessage());
        }
    }

    public WebElement getNoteRow(String noteTitle, String noteDescription) {
        for (WebElement noteElement : noteElements) {
            WebElement titleElement = noteElement.findElement(By.className("note-title"));
            WebElement descriptionElement = noteElement.findElement(By.className("note-description"));

            if (titleElement.getText().equals(noteTitle) && descriptionElement.getText().equals(noteDescription))
                return noteElement;
        }
        return null;
    }
}
