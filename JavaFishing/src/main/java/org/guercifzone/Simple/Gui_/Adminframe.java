package org.guercifzone.Simple.Gui_;

import org.guercifzone.DeepSeek.Gui.LogViewerPanel;
import org.guercifzone.DeepSeek.Gui.ServerPanel;
import org.guercifzone.DeepSeek.Gui.UrlGeneratorPanel;

import javax.swing.*;
import java.awt.*;

public class Adminframe extends JFrame {
    private JTabbedPane tabbedPane;
    public Adminframe() {
        setTitle("Admin Panel");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

      initUi();

    }

    private void initUi() {
        tabbedPane = new JTabbedPane();

        ServerPanel serverPanel = new ServerPanel();
        UrlGeneratorPanel urlGeneratorPanel = new UrlGeneratorPanel();
        LogViewerPanel logViewerPanel = new LogViewerPanel();

        tabbedPane.addTab("Server Control", serverPanel);
        tabbedPane.addTab("URL Generator", urlGeneratorPanel);
        tabbedPane.addTab("Captured Logs", logViewerPanel);

        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);

    }
}
