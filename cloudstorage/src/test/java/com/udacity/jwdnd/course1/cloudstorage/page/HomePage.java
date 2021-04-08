package com.udacity.jwdnd.course1.cloudstorage.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class HomePage {

    @FindBy(id = "logoutBtn")
    private WebElement logoutBtn;

    @FindBy(id = "nav-files-tab")
    private WebElement filesTabButton;

    @FindBy(id = "nav-notes-tab")
    private WebElement notesTabButton;

    @FindBy(id = "nav-credentials-tab")
    private WebElement credentialsTabButton;

    public HomePage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    public void openFilesTab(){
        filesTabButton.click();
    }

    public void openNotesTab(){
        notesTabButton.click();
    }

    public void openCredentialsTab(){
        credentialsTabButton.click();
    }

    public void logout() {
        logoutBtn.click();
    }

}
