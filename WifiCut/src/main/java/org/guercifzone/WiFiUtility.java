package org.guercifzone;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class WiFiUtility extends JFrame {
    private JTable wifiTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    private JButton connectButton;
    private JButton disconnectButton;
    private JTextArea statusArea;
    private JProgressBar signalStrengthBar;

    public WiFiUtility() {
        setTitle("WiFi Connection Utility");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initializeComponents();
        setupLayout();
        loadAvailableNetworks();

        setVisible(true);
    }

    private void initializeComponents() {
        // Table setup
        String[] columns = {"SSID", "Signal Strength", "Security", "BSSID"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        wifiTable = new JTable(tableModel);
        wifiTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Buttons
        refreshButton = new JButton("Refresh Networks");
        connectButton = new JButton("Connect");
        disconnectButton = new JButton("Disconnect");
        connectButton.setEnabled(false);
        disconnectButton.setEnabled(false);

        // Status area
        statusArea = new JTextArea();
        statusArea.setEditable(false);
        statusArea.setBackground(new Color(240, 240, 240));

        // Signal strength bar
        signalStrengthBar = new JProgressBar(0, 100);
        signalStrengthBar.setStringPainted(true);
        signalStrengthBar.setString("Signal Strength");

        // Add event listeners
        addEventListeners();
    }

    private void setupLayout() {
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table in scroll pane
        JScrollPane tableScrollPane = new JScrollPane(wifiTable);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.add(refreshButton);
        buttonPanel.add(connectButton);
        buttonPanel.add(disconnectButton);
        buttonPanel.add(signalStrengthBar);

        mainPanel.add(buttonPanel, BorderLayout.NORTH);

        // Status panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createTitledBorder("Connection Status"));
        statusPanel.add(new JScrollPane(statusArea), BorderLayout.CENTER);

        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void addEventListeners() {
        refreshButton.addActionListener(e -> loadAvailableNetworks());

        connectButton.addActionListener(e -> connectToSelectedNetwork());

        disconnectButton.addActionListener(e -> disconnectFromNetwork());

        wifiTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = wifiTable.getSelectedRow();
                if (selectedRow != -1) {
                    connectButton.setEnabled(true);
                    updateSignalStrength(selectedRow);
                } else {
                    connectButton.setEnabled(false);
                }
            }
        });
    }

    private void loadAvailableNetworks() {
        tableModel.setRowCount(0);
        statusArea.setText("Scanning for available networks...");

        SwingWorker<List<WiFiNetwork>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<WiFiNetwork> doInBackground() throws Exception {
                return scanForNetworks();
            }

            @Override
            protected void done() {
                try {
                    List<WiFiNetwork> networks = get();
                    for (WiFiNetwork network : networks) {
                        tableModel.addRow(new Object[]{
                                network.getSsid(),
                                network.getSignalStrength() + "%",
                                network.getSecurity(),
                                network.getBssid()
                        });
                    }
                    statusArea.setText("Found " + networks.size() + " networks. Select a network to connect.");
                } catch (Exception ex) {
                    statusArea.setText("Error scanning networks: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private List<WiFiNetwork> scanForNetworks() {
        List<WiFiNetwork> networks = new ArrayList<>();

        try {
            // Simulate network scanning (replace with actual system commands)
            // For Windows: netsh wlan show networks mode=bssid
            // For Linux: nmcli dev wifi list
            // For Mac: /System/Library/PrivateFrameworks/Apple80211.framework/Versions/Current/Resources/airport -s

            // This is a simulation - in real application, you'd parse actual command output
            networks.add(new WiFiNetwork("HomeNetwork", 85, "WPA2", "AA:BB:CC:DD:EE:FF"));
            networks.add(new WiFiNetwork("OfficeWiFi", 92, "WPA2-Enterprise", "11:22:33:44:55:66"));
            networks.add(new WiFiNetwork("GuestNetwork", 45, "Open", "FF:EE:DD:CC:BB:AA"));
            networks.add(new WiFiNetwork("MyHotspot", 78, "WPA3", "12:34:56:78:90:AB"));

            Thread.sleep(1000); // Simulate scan time

        } catch (Exception e) {
            throw new RuntimeException("Scan failed: " + e.getMessage());
        }

        return networks;
    }

    private void connectToSelectedNetwork() {
        int selectedRow = wifiTable.getSelectedRow();
        if (selectedRow == -1) return;

        String ssid = (String) tableModel.getValueAt(selectedRow, 0);
        String security = (String) tableModel.getValueAt(selectedRow, 2);

        // Show password dialog for secured networks
        if (!"Open".equals(security)) {
            String password = JOptionPane.showInputDialog(this,
                    "Enter password for " + ssid + ":", "WiFi Password", JOptionPane.QUESTION_MESSAGE);

            if (password == null || password.trim().isEmpty()) {
                statusArea.setText("Connection cancelled: No password provided");
                return;
            }

            connectToNetwork(ssid, password);
        } else {
            connectToNetwork(ssid, null);
        }
    }

    private void connectToNetwork(String ssid, String password) {
        statusArea.setText("Connecting to " + ssid + "...");
        connectButton.setEnabled(false);

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                // Simulate connection process
                try {
                    Thread.sleep(2000); // Simulate connection time

                    // In real application, you would execute system commands:
                    // Windows: netsh wlan connect name="SSID"
                    // Linux: nmcli dev wifi connect "SSID" password "password"
                    // Mac: networksetup -setairportnetwork en0 "SSID" "password"

                    return true; // Simulate successful connection
                } catch (InterruptedException e) {
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        statusArea.setText("Successfully connected to " + ssid);
                        disconnectButton.setEnabled(true);
                    } else {
                        statusArea.setText("Failed to connect to " + ssid);
                        connectButton.setEnabled(true);
                    }
                } catch (Exception ex) {
                    statusArea.setText("Connection error: " + ex.getMessage());
                    connectButton.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void disconnectFromNetwork() {
        statusArea.setText("Disconnecting...");

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    Thread.sleep(1000); // Simulate disconnection time

                    // In real application:
                    // Windows: netsh wlan disconnect
                    // Linux: nmcli dev disconnect iface wlan0
                    // Mac: networksetup -setairportpower en0 off

                    return true;
                } catch (InterruptedException e) {
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        statusArea.setText("Disconnected successfully");
                        disconnectButton.setEnabled(false);
                    } else {
                        statusArea.setText("Failed to disconnect");
                    }
                } catch (Exception ex) {
                    statusArea.setText("Disconnection error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void updateSignalStrength(int row) {
        String strengthStr = (String) tableModel.getValueAt(row, 1);
        int strength = Integer.parseInt(strengthStr.replace("%", ""));
        signalStrengthBar.setValue(strength);
        signalStrengthBar.setString("Signal: " + strength + "%");

        // Color coding based on signal strength
        if (strength > 75) {
            signalStrengthBar.setForeground(Color.GREEN);
        } else if (strength > 50) {
            signalStrengthBar.setForeground(Color.ORANGE);
        } else if (strength > 25) {
            signalStrengthBar.setForeground(Color.YELLOW);
        } else {
            signalStrengthBar.setForeground(Color.RED);
        }
    }

    // WiFi Network data class
    private static class WiFiNetwork {
        private String ssid;
        private int signalStrength;
        private String security;
        private String bssid;

        public WiFiNetwork(String ssid, int signalStrength, String security, String bssid) {
            this.ssid = ssid;
            this.signalStrength = signalStrength;
            this.security = security;
            this.bssid = bssid;
        }

        public String getSsid() { return ssid; }
        public int getSignalStrength() { return signalStrength; }
        public String getSecurity() { return security; }
        public String getBssid() { return bssid; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new WiFiUtility();
        });
    }
}