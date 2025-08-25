package org.guercifzone.Utils;


import java.awt.Color;

public class ColorUtils {

    public static final Color FG_RED = Color.RED;
    public static final Color FG_GREEN = new Color(0, 128, 0); // Dark green
    public static final Color FG_DEFAULT = Color.BLACK;

    public static String colorize(String text, Color color) {
        // For console output, we can't use colors directly in Java
        // This would be handled by the GUI instead
        return text;
    }
}