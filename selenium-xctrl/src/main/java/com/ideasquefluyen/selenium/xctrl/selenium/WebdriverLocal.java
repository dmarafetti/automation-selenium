package com.ideasquefluyen.selenium.xctrl.selenium;

import org.openqa.selenium.WebDriver;

/**
 *
 *
 *
 * @author dmarafetti
 * @since 1.0.0
 *
 */
public class WebdriverLocal {


	/**
	 *
	 *
	 */
	private static ThreadLocal<WebDriver> threadScope = new ThreadLocal<WebDriver>() {

		@Override
		protected WebDriver initialValue() {

			return super.initialValue();
		}

	};


	/**
	 *
	 * @param aDriver
	 */
	public static void setDriver(String browser) {

		try {

			threadScope.set(WebdriverFactory.getInstance(browser));

		} catch (Exception ex) {

			throw new RuntimeException(ex);
		}
	}


	/**
	 *
	 * @return
	 */
	public static WebDriver getDriver() {

		return threadScope.get();
	}


}
