/**
 *
 */
package com.ideasquefluyen.selenium.xctrl.database;

import java.util.List;

/**
 * Dao for the enrollment application
 *
 * @author dmarafetti
 * @since 1.0.1
 *
 */
public interface ApplicationRepository {


    /**
     *
     *
     * @param token
     * @return List of application ids
     */
    public List<String> getApplicationIdByToken(String token);

}
