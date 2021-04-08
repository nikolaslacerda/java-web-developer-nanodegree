package com.udacity.jwdnd.course1.cloudstorage.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ResultPage {

    @FindBy(id = "success-msg")
    private WebElement successMessage;

    @FindBy(id = "error-msg")
    private WebElement errorMessage;

    @FindBy(id = "home-success")
    private WebElement homeSuccessLink;

    @FindBy(id = "home-error")
    private WebElement homeErrorLink;

    public ResultPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    public boolean isSuccessful() {
        return this.successMessage.isDisplayed();
    }

    public boolean isError() {
        return this.errorMessage.isDisplayed();
    }

    public void clickToReturnHomeSuccess() {
        this.homeSuccessLink.click();
    }

    public void clickToReturnHomeError() {
        this.homeErrorLink.click();
    }
}

