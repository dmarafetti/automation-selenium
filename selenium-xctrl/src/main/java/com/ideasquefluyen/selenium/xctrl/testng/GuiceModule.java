package com.ideasquefluyen.selenium.xctrl.testng;

import org.nnsoft.guice.rocoto.Rocoto;
import org.nnsoft.guice.rocoto.configuration.ConfigurationModule;

import com.ideasquefluyen.selenium.xctrl.testng.screenshot.ScreenshotService;
import com.ideasquefluyen.selenium.xctrl.testng.screenshot.ScreenshotServiceFilesystemImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;


/**
 * Bind classes for dependecy injection.
 *
 * @see {@link AbstractModule}
 * @author dmarafetti
 *
 */
public class GuiceModule extends AbstractModule {

	@Override
	protected void configure() {

		install(Rocoto.expandVariables(new ConfigurationModule() {

			@Override
			protected void bindConfigurations() {

				bindEnvironmentVariables();
				bindSystemProperties();
			}

		}));

		bind(ScreenshotService.class).to(ScreenshotServiceFilesystemImpl.class).in(Singleton.class);
	}
}
