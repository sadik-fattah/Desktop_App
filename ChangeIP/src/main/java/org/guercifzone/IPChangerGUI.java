package org.guercifzone;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class IPChangerGUI extends JFrame {
    private JTextField interfaceField;
    private JTextField ipAddressField;
    private JTextField subnetField;
    private JTextField gatewayField;
    private JTextArea outputArea;
    private JComboBox<String> interfaceComboBox;

    public IPChangerGUI() {
        initializeUI();
        loadNetworkInterfaces();
    }

    private void initializeUI() {
        setTitle("IP Address Changer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Interface selection
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Network Interface:"), gbc);

        interfaceComboBox = new JComboBox<>();
        interfaceComboBox.setPreferredSize(new Dimension(200, 25));
        gbc.gridx = 1; gbc.gridy = 0;
        inputPanel.add(interfaceComboBox, gbc);

        // IP Address
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("IP Address:"), gbc);

        ipAddressField = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 1;
        inputPanel.add(ipAddressField, gbc);

        // Subnet Mask
        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("Subnet Mask:"), gbc);

        subnetField = new JTextField(15);
        subnetField.setText("255.255.255.0");
        gbc.gridx = 1; gbc.gridy = 2;
        inputPanel.add(subnetField, gbc);

        // Default Gateway
        gbc.gridx = 0; gbc.gridy = 3;
        inputPanel.add(new JLabel("Default Gateway:"), gbc);

        gatewayField = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 3;
        inputPanel.add(gatewayField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton setStaticBtn = new JButton("Set Static IP");
        JButton setDHCPBtn = new JButton("Set DHCP");
        JButton refreshBtn = new JButton("Refresh Interfaces");
        JButton checkIPBtn = new JButton("Check Current IP");

        setStaticBtn.addActionListener(this::setStaticIP);
        setDHCPBtn.addActionListener(this::setDHCP);
        refreshBtn.addActionListener(e -> loadNetworkInterfaces());
        checkIPBtn.addActionListener(this::checkCurrentIP);

        buttonPanel.add(setStaticBtn);
        buttonPanel.add(setDHCPBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(checkIPBtn);

        // Output area
        outputArea = new JTextArea(10, 50);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Output"));

        // Add components to main panel
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private void setStaticIP(ActionEvent e) {
        String interfaceName = (String) interfaceComboBox.getSelectedItem();
        String ip = ipAddressField.getText();
        String subnet = subnetField.getText();
        String gateway = gatewayField.getText();

        if (interfaceName == null || interfaceName.isEmpty()) {
            showError("Please select a network interface");
            return;
        }

        if (!isValidIP(ip) || !isValidIP(subnet) || !isValidIP(gateway)) {
            showError("Please enter valid IP addresses");
            return;
        }

        executeIPChangeCommand(interfaceName, ip, subnet, gateway, false);
    }

    private void setDHCP(ActionEvent e) {
        String interfaceName = (String) interfaceComboBox.getSelectedItem();

        if (interfaceName == null || interfaceName.isEmpty()) {
            showError("Please select a network interface");
            return;
        }

        executeIPChangeCommand(interfaceName, null, null, null, true);
    }

    private void executeIPChangeCommand(String interfaceName, String ip,
                                        String subnet, String gateway, boolean useDHCP) {
        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                publish("Starting IP configuration change...");

                try {
                    if (System.getProperty("os.name").toLowerCase().contains("win")) {
                        if (useDHCP) {
                            // Set DHCP on Windows
                            executeCommand("netsh interface ip set address \"" +
                                    interfaceName + "\" dhcp");
                            publish("Set interface to DHCP mode");
                        } else {
                            // Set static IP on Windows
                            executeCommand("netsh interface ip set address \"" +
                                    interfaceName + "\" static " +
                                    ip + " " + subnet + " " + gateway + " 1");
                            publish("Set static IP: " + ip);
                        }
                    } else if (System.getProperty("os.name").toLowerCase().contains("linux")) {
                        if (useDHCP) {
                            // Set DHCP on Linux (requires NetworkManager)
                            executeCommand("sudo nmcli con mod " + interfaceName +
                                    " ipv4.method auto");
                            executeCommand("sudo nmcli con down " + interfaceName);
                            executeCommand("sudo nmcli con up " + interfaceName);
                            publish("Set interface to DHCP mode");
                        } else {
                            // Set static IP on Linux
                            executeCommand("sudo ifconfig " + interfaceName + " " +
                                    ip + " netmask " + subnet);
                            executeCommand("sudo route add default gw " + gateway +
                                    " " + interfaceName);
                            publish("Set static IP: " + ip);
                        }
                    } else {
                        publish("Unsupported operating system");
                    }
                } catch (Exception ex) {
                    publish("Error: " + ex.getMessage());
                }

                return null;
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                for (String message : chunks) {
                    outputArea.append(message + "\n");
                }
            }

            @Override
            protected void done() {
                publish("Operation completed.\n");
                checkCurrentIP(null);
            }
        }.execute();
    }

    private void checkCurrentIP(ActionEvent e) {
        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                publish("Checking current public IP...");

                try {
                    // Method 1: Amazon AWS
                    String ip = getWebContent("http://checkip.amazonaws.com");
                    publish("Public IP: " + ip.trim());

                    // Method 2: Alternative service
                    String ip2 = getWebContent("http://icanhazip.com");
                    publish("Alternative check: " + ip2.trim());

                } catch (Exception ex) {
                    publish("Error checking IP: " + ex.getMessage());
                }

                return null;
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                for (String message : chunks) {
                    outputArea.append(message + "\n");
                }
            }
        }.execute();
    }

    private void loadNetworkInterfaces() {
        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                interfaceComboBox.removeAllItems();
                publish("Loading network interfaces...");

                try {
                    if (System.getProperty("os.name").toLowerCase().contains("win")) {
                        // Windows - get interface names
                        Process process = Runtime.getRuntime().exec("netsh interface show interface");
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(process.getInputStream()));

                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.contains("Connected") || line.contains("Disconnected")) {
                                String[] parts = line.split("\\s+");
                                if (parts.length >= 4) {
                                    String interfaceName = parts[parts.length - 1];
                                    interfaceComboBox.addItem(interfaceName);
                                }
                            }
                        }
                    } else {
                        // Linux - get interface names
                        Process process = Runtime.getRuntime().exec("ip link show");
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(process.getInputStream()));

                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.contains(": ") && !line.contains("lo:")) {
                                String[] parts = line.split(": ");
                                if (parts.length >= 2) {
                                    String interfaceName = parts[1].trim();
                                    if (!interfaceName.isEmpty()) {
                                        interfaceComboBox.addItem(interfaceName);
                                    }
                                }
                            }
                        }
                    }

                    publish("Interfaces loaded successfully");
                } catch (Exception ex) {
                    publish("Error loading interfaces: " + ex.getMessage());
                }

                return null;
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                for (String message : chunks) {
                    outputArea.append(message + "\n");
                }
            }
        }.execute();
    }

    private String executeCommand(String command) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));

        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        return output.toString();
    }

    private String getWebContent(String urlString) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("curl", "-s", urlString);
        Process process = pb.start();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));

        return reader.readLine();
    }

    private boolean isValidIP(String ip) {
        if (ip == null) return false;
        return ip.matches("^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new IPChangerGUI();
        });
    }
}