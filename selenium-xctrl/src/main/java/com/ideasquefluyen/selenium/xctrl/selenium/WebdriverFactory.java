package com.ideasquefluyen.selenium.xctrl.selenium;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.safari.SafariDriver;

import io.appium.java_client.android.AndroidDriver;


/**
 *
 *
 *
 *
 * @author dmarafetti
 * @since 1.0.0
 *
 */
public class WebdriverFactory {


    private static final Logger LOGGER = Logger.getLogger(WebdriverFactory.class.getName());

	/**
	 *
	 *
	 * @param browser
	 * @return
	 * @throws MalformedURLException
	 */
	public static WebDriver getInstance(String browser) throws Exception {

		WebDriver aDriver;

		LOGGER.config("Selected Webdriver: " + browser);


		//
		// Build webdriver exclusively for hybrid application
		// TODO: Only support for Android webview
		//
		if(browser.equals("hybrid")) {

		    String automationEngine = System.getProperty("selenium.hybridEngine");
            String hybridHost       = System.getProperty("selenium.hybridHost");
            String hybridPort       = System.getProperty("selenium.hybridPort");
            String apkName          = System.getProperty("selenium.hybridApk");
            String appPackage       = System.getProperty("selenium.hybridPackage");
            String deviceName       = System.getProperty("selenium.hybridDeviceName");
            String version          = System.getProperty("selenium.hybridAndroidVersion");

            // get apk
            URL apkUrl = ClassLoader.getSystemResource("com/ideasquefluyen/selenium/xctrl/android/" + apkName);

            if(apkUrl == null) {

                throw new RuntimeException("Apk '" + apkName + "' not found on com/ideasquefluyen/selenium/xctrl/android/");
            }



            LOGGER.config("engine: " + automationEngine);
            LOGGER.config("host: " + hybridHost);
            LOGGER.config("port: " + hybridPort);
            LOGGER.config("apk: " + apkName);
            LOGGER.config("Apk path: " + apkUrl.getPath());
            LOGGER.config("device: " + deviceName);


            // build url
            StringBuilder appiumUrl = new StringBuilder();
            appiumUrl.append("http://");
            appiumUrl.append(hybridHost);
            appiumUrl.append(":");
            appiumUrl.append(hybridPort);
            appiumUrl.append("/wd/hub");

            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability("automationName",automationEngine); // One of: Appium || Selendroid
            capabilities.setCapability("deviceName",deviceName);
            capabilities.setCapability("udid",deviceName);
            capabilities.setCapability("platformName","Android");
            capabilities.setCapability("platformVersion", version);
            capabilities.setCapability("app",  apkUrl.getPath());
            capabilities.setCapability("appPackage", appPackage);
            capabilities.setCapability("appActivity", ".MainActivity");
            capabilities.setCapability("appWaitActivity", ".MainActivity");
            capabilities.setCapability("autoWebviewTimeout", "120000");
            capabilities.setCapability("newCommandTimeout", "60");


            // try 5 times to connect
            byte count = 1;

            System.out.print("Trying to reach Appium.");

            do {

                try {

                    aDriver = new AndroidDriver<WebElement>(new URL(appiumUrl.toString()), capabilities);

                    System.out.println(" OK!");

                    return aDriver;

                } catch (UnreachableBrowserException ex) {

                    if(10 == count) {

                        System.out.println(" Fail! " + appiumUrl.toString());

                        throw ex;
                    }

                    count++;

                    Thread.sleep(1000);

                    System.out.print(".");

                }

            } while (count <= 10);

        }


		//
		// Desktop browsers
		//
		if(browser.equals("chrome")) {

			aDriver = getChromeDriver();

		} else if(browser.equals("safari")) {

			aDriver = getSafariDriver();

		} else if(browser.equals("grid")){

			String hubUrl = System.getProperty("selenium.gridUrl");

			aDriver = getRemoteDriver(new URL(hubUrl));

		} else {

			ProfilesIni profile = new ProfilesIni();

			FirefoxProfile myprofile = profile.getProfile("SeleniumFirefoxProfile");

			aDriver = new FirefoxDriver(myprofile);
		}

		//
		// configure driver here
		//
		aDriver.manage().timeouts().setScriptTimeout(120, TimeUnit.SECONDS);
		aDriver.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);

		return aDriver;
	}



	/**
	 *
	 * @return
	 */
	public static WebDriver getChromeDriver() {

		return new ChromeDriver();
	}

	/**
	 *
	 * @return
	 */
	public static WebDriver getFirefoxDriver() {

		return new FirefoxDriver();
	}

	/**
	 *
	 * @return
	 */
	public static WebDriver getSafariDriver() {

		return new SafariDriver();
	}

	/**
	 *
	 * @return
	 */
	public static WebDriver getRemoteDriver(URL url) {

		DesiredCapabilities ffCapability = new DesiredCapabilities();
		ffCapability.setBrowserName("firefox");
		ffCapability.setPlatform(Platform.ANY);

		// set remote driver properties
		RemoteWebDriver webdriver = new RemoteWebDriver(url, ffCapability);
		webdriver.setFileDetector(new LocalFileDetector());

		return webdriver;
	}
}
