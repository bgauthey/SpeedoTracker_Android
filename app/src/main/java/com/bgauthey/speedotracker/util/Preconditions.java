package com.bgauthey.speedotracker.util;

/**
 * Utility class to check condition(s) on object.
 */
public class Preconditions {

    private Preconditions() {
    }

    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        } else {
            return reference;
        }
    }
}
