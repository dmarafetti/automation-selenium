package com.ideasquefluyen.selenium.xctrl.testng.appium;

import java.util.logging.Logger;

import javax.inject.Inject;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.xml.XmlSuite;

import com.ideasquefluyen.selenium.xctrl.selenium.WebdriverLocal;
import com.ideasquefluyen.selenium.xctrl.testng.CachedModuleFactory;
import com.ideasquefluyen.selenium.xctrl.testng.listener.CurrentSuiteContext;
import com.ideasquefluyen.selenium.xctrl.testng.listener.CurrentTestContext;
import com.ideasquefluyen.selenium.xctrl.testng.listener.SeleniumTestNGMethodListener;
import com.ideasquefluyen.selenium.xctrl.testng.screenshot.ScreenshotService;

import io.appium.java_client.AppiumDriver;


/**
 *
 *
 * @author dmarafetti
 * @since 1.0.0
 *
 */
@Listeners({CurrentSuiteContext.class, CurrentTestContext.class, SeleniumTestNGMethodListener.class})
public class AppiumAndroidHybridTest {

    private static final Logger LOGGER = Logger.getLogger(AppiumAndroidHybridTest.class.getName());


    @Inject
    protected ScreenshotService screenshotService;


    /**
     * Inject members explicity
     * @see {@link CachedModuleFactory}
     *
     */
    public AppiumAndroidHybridTest() {

        CachedModuleFactory.getInjector().injectMembers(this);
    }



    /**
     * Find and set the webview for the current hybrid application
     * before each test.
     * @throws InterruptedException
     *
     */
    @BeforeTest
    protected void setWebView() {

        if(!AppiumContext.switchDriverToWebview()) {

            throw new RuntimeException("WEBVIEW context cound not be setted.");
        }
    }


    /**
     *
     * @see {@link BeforeTest}
     */
    @BeforeTest(alwaysRun=true)
    @Parameters({"selenium.browser"})
    protected void buildWebdriverInstance(String browser) throws Exception {

        LOGGER.config("Base class: " + this.hashCode());

        WebdriverLocal.setDriver(browser);
    }


    /**
     *
     * @see {@link AfterSuite}
     */
    @AfterTest(alwaysRun=true)
    public void tearDownWebdriverInstance() {

        LOGGER.config("After test CLOSE DRIVER. " + WebdriverLocal.getDriver().hashCode() + " - CLASS " + this.hashCode());

        getDriver().closeApp();
    }


    @BeforeSuite(alwaysRun=true)
    @Parameters({"selenium.threadsPool"})
    public void setupBeforeSuite(@Optional("1") String poolsize, ITestContext context) {

        Integer size = 1;

        try {

            size = Integer.valueOf(poolsize);

        // I don't need to catch up this exception
        // since size is defaulted to 1 thread.
        } catch(NumberFormatException ex) {}

        if(size < 0) throw new IllegalStateException("Thread pool size must be a positive number.");

        XmlSuite suite = context.getSuite().getXmlSuite();
        suite.setPreserveOrder(XmlSuite.DEFAULT_PRESERVE_ORDER);
        suite.setParallel(XmlSuite.DEFAULT_PARALLEL);
        suite.setThreadCount(XmlSuite.DEFAULT_THREAD_COUNT);
        suite.setVerbose(2);

        if(size > 1) {

            LOGGER.config("Using parallel suite with concurrency equals to " + size);

            suite.setParallel(XmlSuite.ParallelMode.TESTS);
            suite.setThreadCount(size);
        }
    }


    /**
     * Get an instance of an explicit wait
     *
     * @return
     */
    protected WebDriverWait waitFor() {

        return new WebDriverWait(getDriver(), 120);
    }


    /**
     *
     *
     * @param condition
     */
    protected WebElement waitForElement(By condition) {

        String conditionString = condition.toString();

        // wait for popup
        System.out.print("waiting element [" + conditionString + "]... ");

        WebElement el;

        try {

            el = waitFor().until(ExpectedConditions.elementToBeClickable(condition));
            System.out.println("OK");
            return el;

        } catch (TimeoutException ex) {

            System.out.println("NOT_FOUND");
            Assert.fail(conditionString + " not found.", ex);

            // crappy java. Since assert.fail actually throws an Error,
            // this statement will never be executed. The compiler makes me
            // return something.
            return null;
        }
    }


    /**
     *
     * @param millis
     */
    protected void waitThread(long millis) {

        try {

            Thread.sleep(millis);

        } catch (InterruptedException e) {}

    }


    protected WebElement scrollTo(By locale) {

        WebElement el = getDriver().findElement(locale);

        return scrollTo(el);
    }


    protected WebElement scrollTo(WebElement el) {

        getDriver().executeScript("arguments[0].scrollIntoView();", el);

        return el;
    }



    /**
     * Get current webdriver instance
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    protected AppiumDriver<WebElement> getDriver() {

        return (AppiumDriver<WebElement>) WebdriverLocal.getDriver();
    }



    /**
     * Take a screenshot and store the image using
     * {@link ScreenshotRepository}
     *
     */
    protected void takeScreenshot() {

        LOGGER.config("Taking a snapshot!");

        AppiumContext.runOnNativeContext( () -> {

            screenshotService.snapshot();

        });
    }

}
