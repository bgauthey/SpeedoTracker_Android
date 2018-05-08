package com.bgauthey.speedotracker.util;

/**
 * Utility class to check condition(s) on object.
 *
 * @author bgauthey created on 08/05/2018.
 */
public class Preconditions {

    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        } else {
            return reference;
        }
    }
}
