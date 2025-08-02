package org.guercifzone.deepseek;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class PDF2JohnSwing extends JFrame {
    private JTextArea outputArea;
    private JButton selectFileButton;
    private JButton saveOutputButton;

    public PDF2JohnSwing() {
        setTitle("PDF2John - Java Swing Version");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create components
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        selectFileButton = new JButton("Select PDF File");
        saveOutputButton = new JButton("Save Hash Output");

        // Add action listeners
        selectFileButton.addActionListener(e -> selectPDFFile());
        saveOutputButton.addActionListener(e -> saveOutput());

        // Create panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(selectFileButton);
        buttonPanel.add(saveOutputButton);

        // Add components to frame
        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void selectPDFFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select PDF File");

        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            processPDF(selectedFile);
        }
    }

    private void processPDF(File pdfFile) {
        try {
            // Here you would implement the actual PDF parsing and hash extraction
            // This is a simplified placeholder

            // Simulate hash extraction (replace with actual implementation)
            String hashInfo = "PDF hash information extracted from: " + pdfFile.getName() + "\n";
            hashInfo += "Sample hash format for John the Ripper:\n";
            hashInfo += "$pdf$2*3*128*-1028*1*16*0123456789abcdef*32*...";

            outputArea.setText(hashInfo);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error processing PDF: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveOutput() {
        if (outputArea.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No output to save",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Hash Output");

        int returnValue = fileChooser.showSaveDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File outputFile = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(outputFile)) {
                writer.write(outputArea.getText());
                JOptionPane.showMessageDialog(this, "Output saved successfully",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PDF2JohnSwing app = new PDF2JohnSwing();
            app.setVisible(true);
        });
    }
}