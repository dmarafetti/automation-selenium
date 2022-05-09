package com.ideasquefluyen.selenium.xctrl.testng.listener;

import java.util.function.Consumer;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.xml.XmlTest;

import com.ideasquefluyen.selenium.xctrl.testng.CachedModuleFactory;
import com.ideasquefluyen.selenium.xctrl.testng.appium.AppiumContext;
import com.ideasquefluyen.selenium.xctrl.testng.screenshot.ScreenshotService;

/**
 * Test context. One per thread.
 *
 *
 * @author dmarafetti
 * @since 1.0.0
 *
 */
public class CurrentTestContext implements ITestListener {

    private static final Logger LOGGER = Logger.getLogger(CurrentTestContext.class.getName());

    /** valid during each test execution */
    private static final ThreadLocal<ITestNGMethod> testContextTLS = new ThreadLocal<ITestNGMethod>();

    @Inject
    protected ScreenshotService screenshotService;


    /**
     * Class constructor. Must inject member because of
     * <a href="https://github.com/cbeust/testng/issues/279"> https://github.com
     * /cbeust/testng/issues/279</a>
     */
    public CurrentTestContext() {

        CachedModuleFactory.getInjector().injectMembers(this);
    }

    /**
     * Consume testNG method on expression context
     *
     * @return
     */
    public static void runOn(Consumer<ITestNGMethod> c) {

        c.accept(testContextTLS.get());
    }


    /**
     * Has parameter snapshotOnFailed on true?
     *
     * @return
     */
    private boolean isSnapshotOnFailedEnabled(XmlTest test) {

        if (test == null)
            return false;

        String value = test.getAllParameters().get("snapshotOnFail");

        return value != null && value.equals("true");
    }


    /*
     * (non-Javadoc)
     *
     * @see org.testng.ITestListener#onTestStart(org.testng.ITestResult)
     */
    public void onTestStart(ITestResult result) {

        LOGGER.fine("Adding current test method to TLS.");

        ITestNGMethod method = result.getMethod();

        testContextTLS.set(method);

    }

    /*
     * (non-Javadoc)
     *
     * @see org.testng.ITestListener#onTestSuccess(org.testng.ITestResult)
     */
    public void onTestSuccess(ITestResult result) {

        LOGGER.fine("Removing current test method from TLS.");

        testContextTLS.remove();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.testng.ITestListener#onTestFailure(org.testng.ITestResult)
     */
    public void onTestFailure(ITestResult result) {

        if (isSnapshotOnFailedEnabled(result.getTestContext().getCurrentXmlTest())) {

            AppiumContext.runOnNativeContext(() -> {

                screenshotService.snapshot();
            });
        }

        LOGGER.fine("Removing current test method from TLS.");

        testContextTLS.remove();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.testng.ITestListener#onTestSkipped(org.testng.ITestResult)
     */
    public void onTestSkipped(ITestResult result) {

        LOGGER.fine("Removing current test method from TLS.");

        testContextTLS.remove();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.testng.ITestListener#onTestFailedButWithinSuccessPercentage(org.
     * testng.ITestResult)
     */
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

        LOGGER.fine("Removing current test method from TLS.");

        testContextTLS.remove();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.testng.ITestListener#onStart(org.testng.ITestContext)
     */
    public void onStart(ITestContext context) {}


    /*
     * (non-Javadoc)
     *
     * @see org.testng.ITestListener#onFinish(org.testng.ITestContext)
     */
    public void onFinish(ITestContext context) {}

}
