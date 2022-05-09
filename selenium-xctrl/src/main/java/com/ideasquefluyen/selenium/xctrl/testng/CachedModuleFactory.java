package com.ideasquefluyen.selenium.xctrl.testng;

import com.google.inject.Guice;
import com.google.inject.Injector;


/**
 * Share tha same injector through all test runners. Since is not possible from
 * native TestNG configuration we inject them programatically in each base test
 * class.
 *
 * @see {@link https://github.com/cbeust/testng/issues/398}
 * @author dmarafetti
 * @since 1.0.0
 *
 */
public class CachedModuleFactory  {

	private static Injector CACHED_INJECTOR = Guice.createInjector(new GuiceModule());

	public static Injector getInjector() {

		return CACHED_INJECTOR;
	}

}
