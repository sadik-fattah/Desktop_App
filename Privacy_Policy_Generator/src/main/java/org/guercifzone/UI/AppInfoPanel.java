package org.guercifzone.UI;

import javax.swing.*;
import java.awt.*;

public class AppInfoPanel extends JPanel {
    private JTextField appNameField;
    private JTextField developerNameField;
    private JTextField contactEmailField;
    private JTextField websiteField;
    private JTextArea appDescriptionArea;

    public AppInfoPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // App Name
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("App Name*:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        appNameField = new JTextField(30);
        add(appNameField, gbc);

        // Developer Name
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        add(new JLabel("Developer Name*:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        developerNameField = new JTextField(30);
        add(developerNameField, gbc);

        // Contact Email
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Contact Email*:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        contactEmailField = new JTextField(30);
        add(contactEmailField, gbc);

        // Website
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Website (optional):"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        websiteField = new JTextField(30);
        add(websiteField, gbc);

        // App Description
        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("App Description:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2; gbc.gridy = 4;
        appDescriptionArea = new JTextArea(5, 30);
        appDescriptionArea.setLineWrap(true);
        appDescriptionArea.setWrapStyleWord(true);
        add(new JScrollPane(appDescriptionArea), gbc);

        // Add some padding at the bottom
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 3;
        gbc.weighty = 1.0;
        add(Box.createVerticalGlue(), gbc);
    }

    public String getAppName() {
        return appNameField.getText().trim();
    }

    public String getDeveloperName() {
        return developerNameField.getText().trim();
    }

    public String getContactEmail() {
        return contactEmailField.getText().trim();
    }

    public String getWebsite() {
        return websiteField.getText().trim();
    }

    public String getAppDescription() {
        return appDescriptionArea.getText().trim();
    }

    public boolean validateFields() {
        if (getAppName().isEmpty()) {
            JOptionPane.showMessageDialog(this, "App name is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (getDeveloperName().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Developer name is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (getContactEmail().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Contact email is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}

