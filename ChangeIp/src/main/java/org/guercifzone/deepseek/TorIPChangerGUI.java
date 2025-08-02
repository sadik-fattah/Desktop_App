package org.guercifzone.deepseek;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TorIPChangerGUI extends JFrame {

    private static final String TOR_PROXY_HOST = "127.0.0.1";
    private static final int TOR_PROXY_PORT = 9050;
    private static final int TOR_CONTROL_PORT = 9051;
    private static final String TOR_CONTROL_PASSWORD = "your_password";

    private JLabel currentIpLabel;
    private JLabel statusLabel;
    private JButton changeIpButton;
    private JButton checkIpButton;
    private JTextField proxyHostField;
    private JTextField proxyPortField;
    private JPasswordField passwordField;

    public TorIPChangerGUI() {
        setTitle("Tor IP Changer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
        setLocationRelativeTo(null);

        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        currentIpLabel = new JLabel("Current IP: Not checked yet");
        statusLabel = new JLabel("Status: Ready");
        statusLabel.setForeground(Color.BLUE);

        changeIpButton = new JButton("Change IP");
        changeIpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeIp();
            }
        });

        checkIpButton = new JButton("Check Current IP");
        checkIpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkCurrentIp();
            }
        });

        proxyHostField = new JTextField(TOR_PROXY_HOST, 15);
        proxyPortField = new JTextField(String.valueOf(TOR_PROXY_PORT), 5);
        passwordField = new JPasswordField(TOR_CONTROL_PASSWORD, 10);
    }

    private void layoutComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Configuration panel
        JPanel configPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        configPanel.setBorder(BorderFactory.createTitledBorder("Tor Configuration"));
        configPanel.add(new JLabel("Proxy Host:"));
        configPanel.add(proxyHostField);
        configPanel.add(new JLabel("Proxy Port:"));
        configPanel.add(proxyPortField);
        configPanel.add(new JLabel("Control Password:"));
        configPanel.add(passwordField);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(checkIpButton);
        buttonPanel.add(changeIpButton);

        // Status panel
        JPanel statusPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        statusPanel.add(currentIpLabel);
        statusPanel.add(statusLabel);

        mainPanel.add(configPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void checkCurrentIp() {
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                setStatus("Checking current IP...", Color.BLUE);
                return getCurrentIp();
            }

            @Override
            protected void done() {
                try {
                    String ip = get();
                    currentIpLabel.setText("Current IP: " + ip);
                    setStatus("IP check completed", Color.GREEN.darker());
                } catch (Exception e) {
                    setStatus("Error checking IP: " + e.getMessage(), Color.RED);
                }
            }
        }.execute();
    }

    private void changeIp() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                setStatus("Changing IP...", Color.BLUE);

                if (!isTorRunning()) {
                    throw new Exception("Tor is not running");
                }

                String originalIp = getCurrentIp();
                changeTorIp();
                String newIp = getCurrentIp();

                if (originalIp.equals(newIp)) {
                    throw new Exception("IP didn't change. Try again.");
                }

                return null;
            }

            @Override
            protected void done() {
                try {
                    get(); // Check for exceptions
                    checkCurrentIp();
                    setStatus("IP changed successfully", Color.GREEN.darker());
                } catch (Exception e) {
                    setStatus("Error changing IP: " + e.getMessage(), Color.RED);
                }
            }
        }.execute();
    }

    private void setStatus(String message, Color color) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Status: " + message);
            statusLabel.setForeground(color);
        });
    }

    // The original Tor methods from the first example
    private boolean isTorRunning() {
        String host = proxyHostField.getText();
        int port;
        try {
            port = Integer.parseInt(proxyPortField.getText());
        } catch (NumberFormatException e) {
            return false;
        }

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 1000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private String getCurrentIp() throws IOException {
        String host = proxyHostField.getText();
        int port = Integer.parseInt(proxyPortField.getText());

        HttpHost torProxy = new HttpHost(host, port, "socks");
        RequestConfig config = RequestConfig.custom().setProxy(torProxy).build();

        HttpGet request = new HttpGet("https://api.ipify.org?format=json");
        request.setConfig(config);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(request);
             BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {

            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        }
    }

    private void changeTorIp() throws IOException, InterruptedException {
        String host = proxyHostField.getText();
        int port = TOR_CONTROL_PORT;
        String password = new String(passwordField.getPassword());

        try (Socket controlSocket = new Socket(host, port)) {
            // Authenticate
            String authCommand = "AUTHENTICATE \"" + password + "\"\r\n";
            controlSocket.getOutputStream().write(authCommand.getBytes());

            // Send NEWNYM signal
            String newnymCommand = "SIGNAL NEWNYM\r\n";
            controlSocket.getOutputStream().write(newnymCommand.getBytes());

            // Wait for the circuit to be rebuilt
            Thread.sleep(5000);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TorIPChangerGUI gui = new TorIPChangerGUI();
            gui.setVisible(true);
        });
    }
}
