package com.ideasquefluyen.selenium.xctrl.testng.listener;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;
import org.testng.SkipException;

/**
 * With this listener, when a test fails we force the skipping the rest of the suite test
 * since we do not want to continue with the suite. Does not make sence.
 *
 * @author dmarafetti
 * @since 1.0.0
 *
 */
public class SeleniumTestNGMethodListener implements IInvokedMethodListener {

	private Object lock = new Object();

	private static int MAX_FAILURES_COUNT = 1;

	private static String SKIP_MESSAGE = "Skipped due a Selenium exception";

	private static ThreadLocal<Integer> FAILURES = ThreadLocal.withInitial(() -> new Integer(0));


	/*
	 * (non-Javadoc)
	 * @see org.testng.IInvokedMethodListener#beforeInvocation(org.testng.IInvokedMethod, org.testng.ITestResult)
	 */
	public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {

		Integer failuresCounter = FAILURES.get();

		if(!method.getTestMethod().getMethodName().equals("tearDownWebdriverInstance") &&
		   !method.getTestMethod().getMethodName().equals("cleanupTest") &&
		   failuresCounter >= MAX_FAILURES_COUNT) {

			throw new SkipException(SKIP_MESSAGE);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.testng.IInvokedMethodListener#afterInvocation(org.testng.IInvokedMethod, org.testng.ITestResult)
	 */
	public void afterInvocation(IInvokedMethod method, ITestResult testResult) {

		System.out.println(testResult.getName());

		if(testResult.getStatus() == ITestResult.FAILURE) {

			Integer failuresCounter = FAILURES.get();

			synchronized (lock) {

				failuresCounter++;

				FAILURES.set(failuresCounter);
			}
		}
	}

}
