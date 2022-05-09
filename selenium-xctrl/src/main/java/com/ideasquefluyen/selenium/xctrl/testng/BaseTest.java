package com.ideasquefluyen.selenium.xctrl.testng;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.testng.ITestContext;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.xml.XmlTest;

import com.ideasquefluyen.selenium.xctrl.repository.ConfigurationRepository;
import com.ideasquefluyen.selenium.xctrl.testng.selenium.AbstractWebDriverBaseTest;


/**
 * This class gives direct access to <code>configurationRepository</code> and
 * <code>screenshootRepository</code>. Also, inherits selenium capabilities.
 *
 * @author NLorenzo, dmarafetti
 * @since 1.0.0
 *
 */
public class BaseTest extends AbstractWebDriverBaseTest {


	private static final Logger LOGGER = Logger.getLogger(BaseTest.class.getName());



	/** Access to test configuration */
	@Inject
	protected ConfigurationRepository configurationRepository;



	/**
	 * Gets parameters from configuration table using its given index and
	 * store it in test´s context.
	 *
	 * @see {@link BeforeTest}
	 */
	@BeforeTest(alwaysRun=true)
	public void setupTest(ITestContext context) throws URISyntaxException {

		LOGGER.config("Getting parameters from config table.");

		XmlTest xmlTest = context.getCurrentXmlTest();

		// get parameters from config
		Map<String, String> params = configurationRepository.get(xmlTest.getName());

		//Upload the new parameters to the TestNG XML Config file
		params.forEach((key,value) -> {

			xmlTest.addParameter(key, value);

		});
	}


	/**
	 * Clean up test's context to avoid input polution.
	 *
	 * @see {@link AfterTest}
	 */
	@AfterTest(alwaysRun=true)
	public void cleanupTest(ITestContext context) {

		LOGGER.config("Cleanning up test context.");
	}


}



