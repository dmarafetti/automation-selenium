package com.ideasquefluyen.selenium.xctrl.testng.appium;

import java.util.Set;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.ideasquefluyen.selenium.xctrl.selenium.WebdriverLocal;
import com.ideasquefluyen.selenium.xctrl.utils.StringUtils;

import io.appium.java_client.AppiumDriver;

/**
 * Manage device contexts
 *
 * @author dmarafetti
 * @since 1.0.0
 *
 */
public abstract class AppiumContext {


    private static final Logger LOGGER = Logger.getLogger(AppiumContext.class.getName());


    /**
     * Execute a rutine of code on a native context.
     *
     * @param rutine
     */
    public static void runOnNativeContext(Runnable rutine) {

        try {

            switchDriverToNative();
            rutine.run();

        } catch (Exception ex) {

            throw ex;

        } finally {

            switchDriverToWebview();
        }
    }


    /**
     *
     * @return
     */
    public static WebDriver switchDriverToNative() {

        LOGGER.config("switching to native context...");

        return getDriver().context("NATIVE_APP");
    }


    /**
     *
     * @return
     */
    public static Boolean switchDriverToWebview() {

        Integer count = 0;
        Set<String> contextNames = getDriver().getContextHandles();

        LOGGER.config("switching to webview context...");
        LOGGER.config("waiting for webview become available");

        while(!StringUtils.containsContextWithPrefix(contextNames, "WEBVIEW") && count < 10) {

            System.out.print(".");
            waitThread(1000);
            count++;
            contextNames = getDriver().getContextHandles();
        }

        System.out.println(".");

        if(count == 10) {

            LOGGER.config("webview not available");
            return false;
        }

        LOGGER.config("Available contexts: " + contextNames.toString());

        for (String contextName : contextNames) {

            if (contextName.contains("WEBVIEW")) {

                getDriver().context(contextName);
                return true;
            }
        }

        return false;
    }



    /**
     * Get current webdriver instance
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    private static AppiumDriver<WebElement> getDriver() {

        return (AppiumDriver<WebElement>) WebdriverLocal.getDriver();
    }


    /**
     * Block current thread
     *
     * @param millis
     */
    private static void waitThread(long millis) {

        try {

            Thread.sleep(millis);

        } catch (InterruptedException e) {}
    }

}
