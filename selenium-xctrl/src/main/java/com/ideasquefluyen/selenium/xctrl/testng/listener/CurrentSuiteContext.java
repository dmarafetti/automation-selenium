/**
 *
 */
package com.ideasquefluyen.selenium.xctrl.testng.listener;

import java.util.HashMap;
import java.util.Map;

import org.testng.ISuite;
import org.testng.ISuiteListener;

/**
 * @author dmarafetti
 *
 */
public class CurrentSuiteContext implements ISuiteListener {


    /** valid during all suite cycle */
    private static final ThreadLocal<Map<String, Object>> globalParametersTLS = ThreadLocal.withInitial(() -> new HashMap<String, Object>());


    /**
     * Add test parameter at runtime
     *
     * @param key
     * @param value
     */
    public static void setGlobalParameter(String key, Object value) {

        globalParametersTLS.get().put(key, value);

    }


    /**
     * Retrieve test parameter
     *
     * @param key
     * @return
     */
    public static Object getGlobalParameter(String key) {

        return globalParametersTLS.get().get(key);
    }


    /**
     * test parameter exist
     *
     * @param key
     * @return
     */
    public static boolean hasGlobalParameter(String key) {

        return globalParametersTLS.get().containsKey(key);
    }



    /* (non-Javadoc)
     * @see org.testng.ISuiteListener#onStart(org.testng.ISuite)
     */
    public void onStart(ISuite suite) {


    }

    /* (non-Javadoc)
     * @see org.testng.ISuiteListener#onFinish(org.testng.ISuite)
     */
    public void onFinish(ISuite suite) {

        globalParametersTLS.remove();
    }

}
