package com.stackframe.sarariman;

/**
 *
 * @author mcculley
 */
public class HTMLUtils {

    private HTMLUtils() {
    }

    public static boolean containsHTML(String s) {
        // FIXME: Make this handle other elements.
        return s.matches("<p>");
    }

}
