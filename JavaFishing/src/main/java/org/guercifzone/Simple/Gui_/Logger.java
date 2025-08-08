package org.guercifzone.Simple.Gui_;

import org.guercifzone.DeepSeek.CredentialLogger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Logger extends JPanel {
    private JTextArea logArea;

    public Logger() {
        initComponents();
    }

    private void initComponents() {
        add(new JLabel("Captured Credentials", JLabel.CENTER), BorderLayout.NORTH);
        logArea = new JTextArea(20, 50);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        add(scrollPane,BorderLayout.CENTER);
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

    private void clearLogs(ActionEvent actionEvent) {
        String logs = CredentialLogger.getLogsAsString();
        logArea.setText(logs);
        logArea.setCaretPosition(0);
    }

    private void refreshLogs(ActionEvent actionEvent) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to clear all logs?",
                "Confirm Clear", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            CredentialLogger.clearLogs();
            refreshLogs(null);
        }
    }


}
