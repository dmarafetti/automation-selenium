package com.ideasquefluyen.selenium.xctrl;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ideasquefluyen.selenium.xctrl.listener.TestApplicationListener;
import com.ideasquefluyen.selenium.xctrl.testng.appium.AppiumAndroidHybridTest;
import com.ideasquefluyen.selenium.xctrl.testng.appium.AppiumContext;
import com.ideasquefluyen.selenium.xctrl.testng.listener.CurrentSuiteContext;
import com.ideasquefluyen.selenium.xctrl.utils.StringUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidKeyCode;

/**
 * Common steps for Xctrl
 *
 *
 * @author dmarafetti
 * @since 1.0.0
 *
 */
@Listeners({TestApplicationListener.class})
public abstract class CommonSteps extends AppiumAndroidHybridTest {


    @Test
    @Parameters({"env"})
    public void selectEnvironment(String env) {

        waitForElement(By.xpath("//a[contains(@href,'" + env + "')]")).click();
    }

    @Test
    @Parameters({"url"})
    public void selectLocalEnvironment(String url) {

        WebElement input = waitForElement(By.id("ip_txt"));
        input.clear();
        input.sendKeys(url);

        scrollTo(By.xpath("//button[contains(text(), 'Go to IP')]")).click();
    }



    @Test
    @Parameters({"username", "password"})
    public void login(String username, String password) throws Exception {

        waitForElement(By.cssSelector("form[name='loginForm']"));

        WebElement nameEl     = getDriver().findElementByCssSelector("form[name='loginForm'] input[name='userName']");
        WebElement passwordEl = getDriver().findElementByCssSelector("form[name='loginForm'] input[name='password']");
        WebElement submit     = getDriver().findElementByCssSelector("form[name='loginForm'] button[type='submit']");

        nameEl.sendKeys(username);
        passwordEl.sendKeys(password);
        waitThread(1000);
        scrollTo(submit).click();
    }



    @Test
    public void integrityPledge() {

        By buttonBy = By.id("integrity-pledge-accept");
        waitForElement(buttonBy);
        scrollTo(buttonBy).click();
     }



    @Test
    @Parameters({"zipcode"})
    public void enterZipcode(String zipcode) {

        waitForElement(By.xpath("//input[@name='zipCode']")).sendKeys(zipcode);
        getDriver().findElementByCssSelector("form[name='zipCodeForm'] button[type='submit']").click();
    }




    @Test
    @Parameters({"firstname", "lastname", "birthDate", "ssn", "address", "phone"})
    public void fillForm(String firstName, String lastName, String birthDate, String ssn, String address, String phone) {

        waitForElement(By.cssSelector("form[name='personalInfo'] input[name='firstName']"));

        // enter form data
        getDriver().findElementByXPath("//input[@name='firstName']").sendKeys(StringUtils.randomString(8));
        getDriver().findElementByXPath("//input[@name='lastName']").sendKeys(StringUtils.randomString(8));

        scrollTo(By.xpath("//input[@name='dob']")).sendKeys(birthDate);
        getDriver().findElementByXPath("//input[@name='ssn']").sendKeys(ssn);
        getDriver().findElementByXPath("//input[@name='addressline1']").sendKeys(address);

        scrollTo(By.xpath("//input[@name='contactPhone']")).sendKeys(phone);
        scrollTo(By.xpath("//input[@name='offers']")).click();
        scrollTo(By.id("form-button-next")).click();
    }


    public void acceptPopupsFromValidations() {

        // go back and review
        waitForElement(By.id("alert-close")).click();

        scrollTo(By.id("form-button-next")).click();

        // next
        waitForElement(By.id("alert-ok")).click();
    }


    /**
     *
     *
     */
    public void takePhotoFromCamera() {

        AppiumContext.runOnNativeContext(() -> {

            waitThread(3000);
            AndroidDriver<WebElement> mDriver = (AndroidDriver<WebElement>) getDriver();
            mDriver.pressKeyCode(AndroidKeyCode.KEYCODE_CAMERA);

            waitThread(6000);
            WebElement saveButton = mDriver.findElement(By.xpath("//android.widget.LinearLayout[1]/android.widget.FrameLayout[1]/android.widget.LinearLayout[1]/android.widget.LinearLayout[1]/android.widget.LinearLayout[2]/android.widget.Button[1]"));
            TouchAction tImg = new TouchAction(mDriver);
            tImg.tap(saveButton).perform().waitAction();
            waitThread(3000);
        });
    }





}
