package org.guercifzone.DeepSeek.Gui;




import org.guercifzone.DeepSeek.WebServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ServerPanel extends JPanel {
    private WebServer server;
    private JButton startButton;
    private JButton stopButton;
    private JLabel statusLabel;

    public ServerPanel() {
        server = new WebServer();
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(new JLabel("Web Server Control"), gbc);

        // Status label
        gbc.gridy++;
        statusLabel = new JLabel("Server Status: Stopped");
        add(statusLabel, gbc);

        // Buttons
        gbc.gridwidth = 1;
        gbc.gridy++;

        startButton = new JButton("Start Server");
        startButton.addActionListener(this::startServer);
        add(startButton, gbc);

        gbc.gridx++;
        stopButton = new JButton("Stop Server");
        stopButton.setEnabled(false);
        stopButton.addActionListener(this::stopServer);
        add(stopButton, gbc);
    }

    private void startServer(ActionEvent e) {
        server.start();
        statusLabel.setText("Server Status: Running on port 8080");
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
    }

    private void stopServer(ActionEvent e) {
        server.stop();
        statusLabel.setText("Server Status: Stopped");
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
    }
}
