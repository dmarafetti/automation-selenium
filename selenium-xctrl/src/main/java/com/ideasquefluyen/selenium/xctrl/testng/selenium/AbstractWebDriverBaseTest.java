package com.ideasquefluyen.selenium.xctrl.testng.selenium;

import java.util.logging.Logger;

import javax.inject.Inject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.WebDriverWait;
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
import com.ideasquefluyen.selenium.xctrl.testng.listener.CurrentTestContext;
import com.ideasquefluyen.selenium.xctrl.testng.screenshot.ScreenshotService;

/**
 * This abstract class brings access to a Selenium {@link WebDriver} in a
 * thread safe environment.
 *
 *
 * @author dmarafetti
 * @since 1.0.0
 *
 */
@Listeners({CurrentTestContext.class})
public abstract class AbstractWebDriverBaseTest {


	private static final Logger LOGGER = Logger.getLogger(AbstractWebDriverBaseTest.class.getName());


	/** Where screenshot are stored */
	@Inject
	protected ScreenshotService screenshotService;



	/**
	 * Inject members explicity
	 * @see {@link CachedModuleFactory}
	 *
	 */
	public AbstractWebDriverBaseTest() {

		CachedModuleFactory.getInjector().injectMembers(this);
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

		WebdriverLocal.getDriver().close();
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
	 * Get current webdriver instance
	 *
	 * @return
	 */
	protected WebDriver getDriver() {

		return WebdriverLocal.getDriver();
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
	 * Maximize window's browser
	 *
	 * @return
	 */
	protected void maximizeWindow() {

		getDriver().manage().window().maximize();
	}


	/**
	 *
	 * @param xPath
	 * @return
	 */
	protected Boolean elementExists(By by) {
	  try {
		  getDriver().findElement(by);
		  return true;
	  } catch (WebDriverException ex){
		  return false;
	  }
	}


	/**
	 * Upload file
	 *
	 * @param by
	 * @param pathToFile
	 */
	protected void uploadFile(By by, String pathToFile) {

		getDriver().findElement(by).sendKeys(pathToFile);
	}


	/**
	 * Take a screenshot and store the image using
	 * {@link ScreenshotRepository}
	 *
	 */
	protected void takeScreenshot() {

	    screenshotService.snapshot();
	}

}
