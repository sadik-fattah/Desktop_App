package org.guercifzone;

import org.guercifzone.UI.AppInfoPanel;
import org.guercifzone.UI.DataCollectionPanel;
import org.guercifzone.UI.GeneratePanel;
import org.guercifzone.UI.PermissionsPanel;

import javax.swing.*;

public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private AppInfoPanel appInfoPanel;
    private PermissionsPanel permissionsPanel;
    private DataCollectionPanel dataCollectionPanel;
    private GeneratePanel generatePanel;

    public MainFrame() {
        setTitle("Privacy Policy Generator");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        layoutComponents();
    }

    private void layoutComponents() {
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("1. App Info", appInfoPanel);
        tabbedPane.addTab("2. Permissions", permissionsPanel);
        tabbedPane.addTab("3. Data Collection", dataCollectionPanel);
        tabbedPane.addTab("4. Generate Policy", generatePanel);

        add(tabbedPane);
    }

    private void initComponents() {
        appInfoPanel = new AppInfoPanel();
        permissionsPanel = new PermissionsPanel();
        dataCollectionPanel = new DataCollectionPanel();
        generatePanel = new GeneratePanel(appInfoPanel, permissionsPanel, dataCollectionPanel);

    }
}
