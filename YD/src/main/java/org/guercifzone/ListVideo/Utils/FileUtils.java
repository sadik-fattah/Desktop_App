package org.guercifzone.ListVideo.Utils;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {
    public static void saveVideo(String videoUrl, String savePath, String format, String title) {
        // In a real implementation, you would use youtube-dl or similar library
        // This is just a placeholder for the actual download logic

        try {
            String safeTitle = title.replaceAll("[^a-zA-Z0-9.-]", "_");
            String fileName = safeTitle + "." + format;
            Path filePath = Paths.get(savePath, fileName);

            // Create directory if it doesn't exist
            Files.createDirectories(filePath.getParent());

            // Create empty file (simulating download)
            Files.createFile(filePath);

            System.out.println("Saved: " + filePath.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}