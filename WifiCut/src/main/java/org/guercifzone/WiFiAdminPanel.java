package org.guercifzone;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WiFiAdminPanel extends JFrame {
    private JTable devicesTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    private JButton blockButton;
    private JButton unblockButton;
    private JTextArea logArea;
    private JComboBox<String> routerTypeCombo;
    private JTextField routerIpField;
    private JTextField adminUserField;
    private JPasswordField adminPassField;

    public WiFiAdminPanel() {
        setTitle("WiFi Admin Control Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initializeComponents();
        setupLayout();
        loadDefaultSettings();

        setVisible(true);
    }

    private void initializeComponents() {
        // Table setup
        String[] columns = {"Select", "Device Name", "IP Address", "MAC Address", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };
        devicesTable = new JTable(tableModel);
        devicesTable.setRowHeight(25);

        // Buttons
        refreshButton = new JButton("Refresh Devices");
        blockButton = new JButton("Block Selected");
        unblockButton = new JButton("Unblock All");
        blockButton.setEnabled(false);
        unblockButton.setEnabled(false);

        // Log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(new Color(240, 240, 240));
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        // Router settings
        routerTypeCombo = new JComboBox<>(new String[]{"TP-Link", "D-Link", "Asus", "Netgear", "Linksys", "Other"});
        routerIpField = new JTextField("192.168.0.1", 15);
        adminUserField = new JTextField("admin", 15);
        adminPassField = new JPasswordField("admin", 15);

        // Add event listeners
        addEventListeners();
    }

    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Settings panel
        JPanel settingsPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        settingsPanel.setBorder(BorderFactory.createTitledBorder("Router Settings"));

        settingsPanel.add(new JLabel("Router Type:"));
        settingsPanel.add(routerTypeCombo);
        settingsPanel.add(new JLabel("Router IP:"));
        settingsPanel.add(routerIpField);
        settingsPanel.add(new JLabel("Username:"));
        settingsPanel.add(adminUserField);
        settingsPanel.add(new JLabel("Password:"));
        settingsPanel.add(adminPassField);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.add(refreshButton);
        buttonPanel.add(blockButton);
        buttonPanel.add(unblockButton);

        // Table in scroll pane
        JScrollPane tableScrollPane = new JScrollPane(devicesTable);

        // Log panel
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Activity Log"));
        logPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);

        // Layout arrangement
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(settingsPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(logPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void addEventListeners() {
        refreshButton.addActionListener(e -> scanNetworkDevices());

        blockButton.addActionListener(e -> blockSelectedDevices());

        unblockButton.addActionListener(e -> unblockAllDevices());

        // Enable buttons when selection changes
        devicesTable.getModel().addTableModelListener(e -> {
            boolean hasSelection = hasSelectedDevices();
            blockButton.setEnabled(hasSelection);
            unblockButton.setEnabled(true);
        });
    }

    private void loadDefaultSettings() {
        logArea.append("WiFi Admin Panel Initialized\n");
        logArea.append("Please configure your router settings first\n");
    }

    private void scanNetworkDevices() {
        logArea.append("Scanning network for connected devices...\n");
        tableModel.setRowCount(0);

        SwingWorker<List<NetworkDevice>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<NetworkDevice> doInBackground() {
                List<NetworkDevice> devices = new ArrayList<>();

                try {
                    // Method 1: ARP table scanning (works on most systems)
                    Process process;
                    String os = System.getProperty("os.name").toLowerCase();

                    if (os.contains("win")) {
                        process = Runtime.getRuntime().exec("arp -a");
                    } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
                        process = Runtime.getRuntime().exec("arp -n");
                    } else {
                        throw new Exception("Unsupported operating system");
                    }

                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;

                    Pattern pattern = Pattern.compile("([0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+)\\s+([0-9a-fA-F:-]{17})\\s+\\w+");

                    while ((line = reader.readLine()) != null) {
                        Matcher matcher = pattern.matcher(line);
                        if (matcher.find()) {
                            String ip = matcher.group(1);
                            String mac = matcher.group(2).toUpperCase();
                            devices.add(new NetworkDevice("Device_" + devices.size(), ip, mac, "Online"));
                        }
                    }

                    process.waitFor();

                    // Add some simulated devices for demonstration
                    if (devices.isEmpty()) {
                        devices.add(new NetworkDevice("Johns-iPhone", "192.168.0.101", "AA:BB:CC:DD:EE:FF", "Online"));
                        devices.add(new NetworkDevice("LivingRoom-TV", "192.168.0.102", "11:22:33:44:55:66", "Online"));
                        devices.add(new NetworkDevice("Guest-Laptop", "192.168.0.103", "FF:EE:DD:CC:BB:AA", "Online"));
                        devices.add(new NetworkDevice("SmartHome-Hub", "192.168.0.104", "12:34:56:78:90:AB", "Online"));
                    }

                } catch (Exception e) {
                    logArea.append("Scan error: " + e.getMessage() + "\n");
                }

                return devices;
            }

            @Override
            protected void done() {
                try {
                    List<NetworkDevice> devices = get();
                    for (NetworkDevice device : devices) {
                        tableModel.addRow(new Object[]{
                                false,
                                device.getName(),
                                device.getIpAddress(),
                                device.getMacAddress(),
                                device.getStatus()
                        });
                    }
                    logArea.append("Found " + devices.size() + " devices\n");
                    unblockButton.setEnabled(true);
                } catch (Exception ex) {
                    logArea.append("Error: " + ex.getMessage() + "\n");
                }
            }
        };
        worker.execute();
    }

    private boolean hasSelectedDevices() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if ((Boolean) tableModel.getValueAt(i, 0)) {
                return true;
            }
        }
        return false;
    }

    private void blockSelectedDevices() {
        List<String> macAddresses = new ArrayList<>();

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if ((Boolean) tableModel.getValueAt(i, 0)) {
                String mac = (String) tableModel.getValueAt(i, 3);
                macAddresses.add(mac);
                tableModel.setValueAt("Blocked", i, 4);
            }
        }

        if (macAddresses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select devices to block");
            return;
        }

        logArea.append("Blocking " + macAddresses.size() + " devices...\n");

        // Simulate blocking (replace with actual router API calls)
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                for (String mac : macAddresses) {
                    try {
                        // This is where you'd implement actual router API calls
                        // The implementation depends on your router brand and model
                        simulateRouterBlock(mac);
                        Thread.sleep(500);
                    } catch (Exception e) {
                        logArea.append("Error blocking " + mac + ": " + e.getMessage() + "\n");
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                logArea.append("Blocking completed\n");
                JOptionPane.showMessageDialog(WiFiAdminPanel.this,
                        "Devices blocked successfully!");
            }
        };
        worker.execute();
    }

    private void unblockAllDevices() {
        logArea.append("Unblocking all devices...\n");

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    // Simulate unblocking all devices
                    simulateRouterUnblockAll();
                    Thread.sleep(1000);
                } catch (Exception e) {
                    logArea.append("Error unblocking: " + e.getMessage() + "\n");
                }
                return null;
            }

            @Override
            protected void done() {
                // Update table status
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    tableModel.setValueAt("Online", i, 4);
                    tableModel.setValueAt(false, i, 0);
                }
                logArea.append("All devices unblocked\n");
                JOptionPane.showMessageDialog(WiFiAdminPanel.this,
                        "All devices have been unblocked!");
            }
        };
        worker.execute();
    }

    private void simulateRouterBlock(String macAddress) {
        logArea.append("Blocking MAC: " + macAddress + " on " + routerTypeCombo.getSelectedItem() + " router\n");
        // Actual implementation would use router's web interface or API
    }

    private void simulateRouterUnblockAll() {
        logArea.append("Removing all blocks from " + routerTypeCombo.getSelectedItem() + " router\n");
        // Actual implementation would use router's web interface or API
    }

    // Network Device data class
    private static class NetworkDevice {
        private String name;
        private String ipAddress;
        private String macAddress;
        private String status;

        public NetworkDevice(String name, String ipAddress, String macAddress, String status) {
            this.name = name;
            this.ipAddress = ipAddress;
            this.macAddress = macAddress;
            this.status = status;
        }

        public String getName() { return name; }
        public String getIpAddress() { return ipAddress; }
        public String getMacAddress() { return macAddress; }
        public String getStatus() { return status; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new WiFiAdminPanel();
        });
    }
}