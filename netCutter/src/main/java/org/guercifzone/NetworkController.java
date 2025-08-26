package org.guercifzone;


import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class NetworkController extends JFrame {
    private JButton scanButton, limitButton, blockButton, unblockButton, resetButton;
    private JList<String> deviceList;
    private JComboBox<String> ifaceCombo;
    private JTextField rateField;
    private List<String> devices = new ArrayList<>();
    private Map<String, Integer> ipToClass = new HashMap<>();
    private int nextClassId = 10;
    private boolean setupDone = false;

    public NetworkController() {
        if (!"root".equals(System.getProperty("user.name"))) {
            JOptionPane.showMessageDialog(null, "This application must be run as root (use sudo) to execute tc and iptables commands.");
            System.exit(1);
        }

        setTitle("Network Device Controller");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        scanButton = new JButton("Scan Network");
        scanButton.addActionListener(new ScanAction());
        add(scanButton, BorderLayout.NORTH);

        deviceList = new JList<>();
        add(new JScrollPane(deviceList), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new GridLayout(3, 2));
        controlPanel.add(new JLabel("Interface:"));

        ifaceCombo = new JComboBox<>();
        populateInterfaces();
        controlPanel.add(ifaceCombo);

        controlPanel.add(new JLabel("Rate (e.g., 1mbit):"));
        rateField = new JTextField("1mbit");
        controlPanel.add(rateField);

        limitButton = new JButton("Limit Selected");
        limitButton.addActionListener(new LimitAction());
        controlPanel.add(limitButton);

        blockButton = new JButton("Block Selected");
        blockButton.addActionListener(new BlockAction());
        controlPanel.add(blockButton);

        unblockButton = new JButton("Unblock Selected");
        unblockButton.addActionListener(new UnblockAction());
        controlPanel.add(unblockButton);

        resetButton = new JButton("Reset Limits");
        resetButton.addActionListener(new ResetAction());
        controlPanel.add(resetButton);

        add(controlPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void populateInterfaces() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if (ni.isUp() && !ni.isLoopback() && !ni.isVirtual()) {
                    ifaceCombo.addItem(ni.getName());
                }
            }
            if (ifaceCombo.getItemCount() > 0) {
                ifaceCombo.setSelectedIndex(0);
            }
        } catch (SocketException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error listing interfaces: " + e.getMessage());
        }
    }

    private class ScanAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            devices.clear();
            deviceList.setListData(new String[0]);

            try {
                String localIp = getLocalIp();
                if (localIp == null) {
                    JOptionPane.showMessageDialog(null, "Could not find local IP.");
                    return;
                }

                String subnet = localIp.substring(0, localIp.lastIndexOf('.'));

                ExecutorService executor = Executors.newFixedThreadPool(20);
                for (int i = 1; i < 255; i++) {
                    String host = subnet + "." + i;
                    executor.execute(new PingRunnable(host));
                }

                executor.shutdown();
                executor.awaitTermination(1, TimeUnit.MINUTES);

                SwingUtilities.invokeLater(() -> deviceList.setListData(devices.toArray(new String[0])));
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error during scan: " + ex.getMessage());
            }
        }
    }

    private class PingRunnable implements Runnable {
        private String host;

        public PingRunnable(String host) {
            this.host = host;
        }

        @Override
        public void run() {
            try {
                InetAddress address = InetAddress.getByName(host);
                if (address.isReachable(1000)) {
                    String hostname = address.getHostName();
                    if (hostname.equals(host)) {
                        hostname = "Unknown";
                    }
                    devices.add(host + " - " + hostname);
                }
            } catch (IOException e) {
                // Ignore unreachable hosts
            }
        }
    }

    private String getLocalIp() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            if (ni.isLoopback() || !ni.isUp()) continue;

            Enumeration<InetAddress> addresses = ni.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                if (addr.isLoopbackAddress() || addr.isLinkLocalAddress() || addr.isMulticastAddress()) continue;
                String ip = addr.getHostAddress();
                if (ip.contains(".")) { // IPv4
                    return ip;
                }
            }
        }
        return null;
    }

    private String getSelectedIp() {
        String selected = deviceList.getSelectedValue();
        if (selected == null) return null;
        return selected.split(" - ")[0];
    }

    private void execCommand(String cmd) {
        try {
            Process p = Runtime.getRuntime().exec(cmd.split(" "));
            StringBuilder output = new StringBuilder();
            StringBuilder error = new StringBuilder();

            BufferedReader outReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = outReader.readLine()) != null) {
                output.append(line).append("\n");
            }

            BufferedReader errReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((line = errReader.readLine()) != null) {
                error.append(line).append("\n");
            }

            p.waitFor();

            if (p.exitValue() != 0) {
                String msg = "Command failed: " + cmd + "\nOutput: " + output.toString() + "\nError: " + error.toString();
                JOptionPane.showMessageDialog(null, msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error executing: " + cmd + "\n" + e.getMessage());
        }
    }

    private class LimitAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String ip = getSelectedIp();
            if (ip == null) {
                JOptionPane.showMessageDialog(null, "Select a device.");
                return;
            }
            String iface = (String) ifaceCombo.getSelectedItem();
            String rate = rateField.getText();
            if (iface == null || rate.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Select interface and enter rate.");
                return;
            }

            if (!setupDone) {
                execCommand("tc qdisc add dev " + iface + " root handle 1: htb default 30");
                execCommand("tc class add dev " + iface + " parent 1: classid 1:1 htb rate 100mbit burst 6k");
                execCommand("tc class add dev " + iface + " parent 1: classid 1:30 htb rate 100mbit burst 6k");
                setupDone = true;
            }

            Integer cid = ipToClass.get(ip);
            if (cid == null) {
                cid = nextClassId++;
                ipToClass.put(ip, cid);
                execCommand("tc class add dev " + iface + " parent 1:1 classid 1:" + cid + " htb rate " + rate + " ceil " + rate + " burst 6k");
                execCommand("tc qdisc add dev " + iface + " parent 1:" + cid + " sfq perturb 10");
                execCommand("tc filter add dev " + iface + " parent 1: protocol ip prio 16 u32 match ip src " + ip + " flowid 1:" + cid);
            } else {
                execCommand("tc class change dev " + iface + " parent 1:1 classid 1:" + cid + " htb rate " + rate + " ceil " + rate + " burst 6k");
            }
            JOptionPane.showMessageDialog(null, "Bandwidth limited for " + ip);
        }
    }

    private class BlockAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String ip = getSelectedIp();
            if (ip == null) {
                JOptionPane.showMessageDialog(null, "Select a device.");
                return;
            }
            execCommand("iptables -A FORWARD -s " + ip + " -j DROP");
            JOptionPane.showMessageDialog(null, "Blocked " + ip);
        }
    }

    private class UnblockAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String ip = getSelectedIp();
            if (ip == null) {
                JOptionPane.showMessageDialog(null, "Select a device.");
                return;
            }
            execCommand("iptables -D FORWARD -s " + ip + " -j DROP");
            JOptionPane.showMessageDialog(null, "Unblocked " + ip);
        }
    }

    private class ResetAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String iface = (String) ifaceCombo.getSelectedItem();
            if (iface == null) {
                JOptionPane.showMessageDialog(null, "Select interface.");
                return;
            }
            execCommand("tc qdisc del dev " + iface + " root");
            ipToClass.clear();
            setupDone = false;
            JOptionPane.showMessageDialog(null, "Limits reset.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(NetworkController::new);
    }
}