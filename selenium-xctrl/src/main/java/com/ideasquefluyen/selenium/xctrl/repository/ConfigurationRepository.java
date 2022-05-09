package com.ideasquefluyen.selenium.xctrl.repository;

import java.util.Map;

/**
 *
 *
 * @author dmarafetti
 *
 */
public interface ConfigurationRepository {


	/**
	 * Removes all configuration from memory
	 *
	 */
	void cleanUp();


	/**
	 *
	 *
	 * @param index
	 * @return
	 */
	Map<String, String> get(String index);

}
