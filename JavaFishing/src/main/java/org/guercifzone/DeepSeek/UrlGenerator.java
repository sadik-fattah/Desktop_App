package org.guercifzone.DeepSeek;



import java.util.Random;

public class UrlGenerator {
    private static final String BASE_URL = "http://localhost:8080/";
    private static final Random RANDOM = new Random();

    public static String generateUrl(String template) {
        String randomPath = generateRandomPath();
        return BASE_URL + randomPath + "?template=" + template;
    }

    private static String generateRandomPath() {
        return Long.toHexString(RANDOM.nextLong());
    }
}
