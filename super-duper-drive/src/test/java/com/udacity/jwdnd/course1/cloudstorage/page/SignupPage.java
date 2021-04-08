package com.udacity.jwdnd.course1.cloudstorage.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class SignupPage {

    @FindBy(id = "inputFirstName")
    private WebElement inputFirstNameField;

    @FindBy(id = "inputLastName")
    private WebElement inputLastNameField;

    @FindBy(id = "inputUsername")
    private WebElement inputUsernameField;

    @FindBy(id = "inputPassword")
    private WebElement inputPasswordField;

    @FindBy(id = "signupBtn")
    private WebElement signupBtn;

    @FindBy(id = "login-link")
    private WebElement loginLink;

    public SignupPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    public void signup(String firstName, String lastName, String username, String password) {
        this.inputFirstNameField.sendKeys(firstName);
        this.inputLastNameField.sendKeys(lastName);
        this.inputUsernameField.sendKeys(username);
        this.inputPasswordField.sendKeys(password);
        this.signupBtn.click();
    }

    public void goToLogin() {
        loginLink.click();
    }
}
