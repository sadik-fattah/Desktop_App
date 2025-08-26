package org.guercifzone;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class NetworkScanner_grok extends JFrame {
    private JButton scanButton;
    private JList<String> deviceList;
    private List<String> devices = new ArrayList<>();

    public NetworkScanner_grok() {
        setTitle("Network Device Scanner");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        scanButton = new JButton("Scan Network");
        scanButton.addActionListener(new ScanAction());
        add(scanButton, BorderLayout.NORTH);

        deviceList = new JList<>();
        add(new JScrollPane(deviceList), BorderLayout.CENTER);

        setVisible(true);
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
                if (address.isReachable(1000)) { // 1 second timeout
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(NetworkScanner_grok::new);
    }
}