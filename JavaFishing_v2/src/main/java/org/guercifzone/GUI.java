package org.guercifzone;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class GUI extends JFrame {
    private WebServer server;
    private JComboBox<String> templateSelector;
    private JTextField portField;
    private JTextArea logArea;
    private JButton startButton, stopButton, openBrowserButton;
    private JLabel statusLabel;
    private HyperlinkLabel urlLabel;
    private int currentPort;
    private String currentTemplate;

    public GUI() {
        setTitle("Phishing Awareness Tool");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        setupLayout();
    }

    private void initComponents() {
        // Template selector
        String[] templates = {"Facebook", "Instagram", "Google", "Twitter"};
        templateSelector = new JComboBox<>(templates);

        // Port field
        portField = new JTextField("8080", 10);

        // Log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        // Buttons
        startButton = new JButton("Start Server");
        stopButton = new JButton("Stop Server");
        openBrowserButton = new JButton("Open in Browser");
        stopButton.setEnabled(false);
        openBrowserButton.setEnabled(false);

        // Style buttons
        startButton.setBackground(new Color(76, 175, 80));
        startButton.setForeground(Color.WHITE);
        stopButton.setBackground(new Color(244, 67, 54));
        stopButton.setForeground(Color.WHITE);
        openBrowserButton.setBackground(new Color(33, 150, 243));
        openBrowserButton.setForeground(Color.WHITE);

        // Status label
        statusLabel = new JLabel("Status: Stopped");
        statusLabel.setForeground(Color.RED);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // URL label with hyperlink
        urlLabel = new HyperlinkLabel("URL: Not running", "");

        // Add action listeners
        startButton.addActionListener(e -> startServer());
        stopButton.addActionListener(e -> stopServer());
        openBrowserButton.addActionListener(e -> openBrowser());
    }

    private void setupLayout() {
        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Control panel
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBorder(BorderFactory.createTitledBorder("Server Controls"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Template and Start button
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(new JLabel("Template:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        controlPanel.add(templateSelector, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        controlPanel.add(startButton, gbc);

        // Row 1: Port and Stop button
        gbc.gridx = 0; gbc.gridy = 1;
        controlPanel.add(new JLabel("Port:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        controlPanel.add(portField, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        controlPanel.add(stopButton, gbc);

        // Row 2: Open browser button
        gbc.gridx = 2; gbc.gridy = 2;
        controlPanel.add(openBrowserButton, gbc);

        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        statusPanel.setBorder(BorderFactory.createTitledBorder("Server Status"));
        statusPanel.add(statusLabel);
        statusPanel.add(urlLabel);

        // Log panel
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Server Log"));
        logPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);

        // Add components to main panel
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(statusPanel, BorderLayout.CENTER);
        mainPanel.add(logPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void startServer() {
        try {
            currentPort = Integer.parseInt(portField.getText());
            currentTemplate = ((String) templateSelector.getSelectedItem()).toLowerCase();
            String url = "http://localhost:" + currentPort;

            server = new WebServer(currentPort, currentTemplate, this);
            server.start();

            // Update UI state
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            openBrowserButton.setEnabled(true);
            templateSelector.setEnabled(false);
            portField.setEnabled(false);

            statusLabel.setText("Status: Running");
            statusLabel.setForeground(new Color(0, 128, 0)); // Dark green
            urlLabel.setUrl(url);
            urlLabel.setText("URL: http://localhost:" + currentPort);

            log("✓ Server started successfully");
            log("Template: " + currentTemplate);
            log("Port: " + currentPort);
            log("Local URL: http://localhost:" + currentPort);
            log("Waiting for connections...");
            log("----------------------------------------");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid port number (1-65535)",
                    "Invalid Port",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            log("✗ Error starting server: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Failed to start server: " + e.getMessage(),
                    "Server Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void stopServer() {
        if (server != null) {
            server.stop();

            // Update UI state
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            openBrowserButton.setEnabled(false);
            templateSelector.setEnabled(true);
            portField.setEnabled(true);

            statusLabel.setText("Status: Stopped");
            statusLabel.setForeground(Color.RED);
            urlLabel.setText("URL: Not running");
            urlLabel.setUrl("");

            log("✗ Server stopped");
            log("----------------------------------------");
        }
    }

    private void openBrowser() {
        if (server != null) {
            String url = "http://localhost:" + currentPort;
            try {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(new URI(url));
                    log("✓ Opened browser: " + url);
                } else {
                    // Fallback for systems without Desktop support
                    String os = System.getProperty("os.name").toLowerCase();
                    Runtime rt = Runtime.getRuntime();

                    if (os.contains("win")) {
                        rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
                    } else if (os.contains("mac")) {
                        rt.exec("open " + url);
                    } else if (os.contains("nix") || os.contains("nux")) {
                        String[] browsers = {"xdg-open", "google-chrome", "chromium", "firefox"};
                        boolean opened = false;
                        for (String browser : browsers) {
                            try {
                                rt.exec(new String[]{browser, url});
                                opened = true;
                                break;
                            } catch (IOException e) {
                                // Continue to next browser
                            }
                        }
                        if (!opened) {
                            throw new IOException("No browser found");
                        }
                    }
                    log("✓ Opened browser: " + url);
                }
            } catch (IOException | URISyntaxException e) {
                log("✗ Failed to open browser: " + e.getMessage());
                // Show URL in message dialog as fallback
                JOptionPane.showMessageDialog(this,
                        "Please manually open this URL in your browser:\n\n" + url,
                        "Server URL",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append("[" + java.time.LocalTime.now().withNano(0) + "] " + message + "\n");
            // Auto-scroll to bottom
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    public void logCredential(String username, String password) {
        SwingUtilities.invokeLater(() -> {
            String credential = String.format("🚨 CAPTURED CREDENTIAL - Username: %s, Password: %s",
                    username, password);
            logArea.append("[" + java.time.LocalTime.now().withNano(0) + "] " + credential + "\n");
            // Auto-scroll to bottom
            logArea.setCaretPosition(logArea.getDocument().getLength());

            // Show popup notification
            JOptionPane.showMessageDialog(this,
                    "🚨 Credential Captured!\n\n" +
                            "Username: " + username + "\n" +
                            "Password: " + password + "\n\n" +
                            "This is a simulation for educational purposes only.",
                    "Credential Captured",
                    JOptionPane.WARNING_MESSAGE);
        });
    }

    // Inner HyperlinkLabel class
    class HyperlinkLabel extends JLabel {
        private String url;

        public HyperlinkLabel(String text, String url) {
            super(text);
            this.url = url;
            setForeground(Color.BLUE);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setFont(new Font("Arial", Font.PLAIN, 12));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    openUrl();
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    setText("<html><u>URL: http://localhost:" + currentPort + "</u></html>");
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setText("URL: http://localhost:" + currentPort);
                }
            });
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        private void openUrl() {
            if (url == null || url.isEmpty()) {
                return;
            }

            try {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(new URI(url));
                    log("✓ Opened browser via URL click: " + url);
                } else {
                    JOptionPane.showMessageDialog(GUI.this,
                            "Please manually open: " + url,
                            "Server URL",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                log("✗ Failed to open URL: " + e.getMessage());
            }
        }
    }
}