package org.guercifzone.DeepSeek.Gui;


import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;

    public MainFrame() {
        super("Phishing Simulation Tool (Educational Use Only)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        tabbedPane = new JTabbedPane();

        // Create panels
        ServerPanel serverPanel = new ServerPanel();
        UrlGeneratorPanel urlGeneratorPanel = new UrlGeneratorPanel();
        LogViewerPanel logViewerPanel = new LogViewerPanel();

        // Add tabs
        tabbedPane.addTab("Server Control", serverPanel);
        tabbedPane.addTab("URL Generator", urlGeneratorPanel);
        tabbedPane.addTab("Captured Logs", logViewerPanel);

        // Add disclaimer
        JPanel disclaimerPanel = new JPanel();
        disclaimerPanel.setBorder(BorderFactory.createTitledBorder("Important Notice"));
        JLabel disclaimer = new JLabel("<html><center>This tool is for educational purposes only.<br>"
                + "Unauthorized phishing is illegal. Only use with explicit permission.</center></html>");
        disclaimer.setForeground(Color.RED);
        disclaimerPanel.add(disclaimer);

        // Add components to frame
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        add(disclaimerPanel, BorderLayout.SOUTH);
    }
}