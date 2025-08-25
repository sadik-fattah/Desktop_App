package org.guercifzone.Utils;


import java.util.ArrayList;
import java.util.List;

public class StringUtils {

    public static String trim(String input) {
        if (input == null) {
            return "";
        }
        return input.trim();
    }

    public static List<String> split(String input, String delimiter) {
        List<String> tokens = new ArrayList<>();
        if (input == null || input.isEmpty()) {
            return tokens;
        }

        String[] parts = input.split(delimiter);
        for (String part : parts) {
            String trimmed = trim(part);
            if (!trimmed.isEmpty()) {
                tokens.add(trimmed);
            }
        }

        return tokens;
    }
}