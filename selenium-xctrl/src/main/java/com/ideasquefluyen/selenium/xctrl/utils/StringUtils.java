/**
 *
 */
package com.ideasquefluyen.selenium.xctrl.utils;

import java.util.Random;
import java.util.Set;

/**
 *
 *
 * @author dmarafetti
 * @since 1.0.0
 *
 */
public class StringUtils {


    /**
     * Get a random string with a fixed lenght
     *
     * @param length
     * @return
     */
    public static String randomString(final int length) {

        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }


    /**
     * Get the string that match with the prefix
     *
     * @param set
     * @param prefix
     * @return
     */
    public static boolean containsContextWithPrefix(Set<String> set, String prefix) {

        return set.stream().anyMatch(x -> {

            return x.contains(prefix);

        });
    }
}
