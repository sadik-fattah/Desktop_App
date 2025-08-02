package org.guercifzone.chatgpt;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class TorIpChanger extends JFrame {
    private static final String CONTROL_HOST = "127.0.0.1";
    private static final int CONTROL_PORT = 9051;
    private static final String CONTROL_PASSWORD = ""; // If password is set in torrc
    private static final String TOR_PROXY_HOST = "127.0.0.1";
    private static final int TOR_PROXY_PORT = 9050;

    private JLabel ipLabel;
    private JButton changeIPButton;

    public void TorIpChanger() {
        setTitle("Tor IP Changer");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        ipLabel = new JLabel("Current IP: Loading...", SwingConstants.CENTER);
        ipLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        changeIPButton = new JButton("Change Tor IP");

        changeIPButton.addActionListener(e -> new Thread(this::changeIP).start());

        add(ipLabel, BorderLayout.CENTER);
        add(changeIPButton, BorderLayout.SOUTH);

        new Thread(this::updateIP).start();
    }

    private void changeIP() {
        try {
            sendTorNewnymSignal();
            Thread.sleep(5000); // Wait for new IP to activate
            updateIP();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error changing IP: " + ex.getMessage());
        }
    }

    private void updateIP() {
        String ip = getCurrentIP();
        if (ip != null) {
            ipLabel.setText("Current IP: " + ip);
        } else {
            ipLabel.setText("Failed to get IP");
        }
    }

    private void sendTorNewnymSignal() throws IOException {
        Socket socket = new Socket(CONTROL_HOST, CONTROL_PORT);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        writer.write("AUTHENTICATE \"" + CONTROL_PASSWORD + "\"\r\n");
        writer.flush();
        String response = reader.readLine();
        if (!response.contains("250")) {
            throw new IOException("Authentication failed: " + response);
        }

        writer.write("SIGNAL NEWNYM\r\n");
        writer.flush();
        response = reader.readLine();
        if (!response.contains("250")) {
            throw new IOException("SIGNAL NEWNYM failed: " + response);
        }

        writer.write("QUIT\r\n");
        writer.flush();
        socket.close();
    }

    private String getCurrentIP() {
        try {
            Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(TOR_PROXY_HOST, TOR_PROXY_PORT));
            URL url = new URL("https://api.ipify.org"); // Can use any IP echo service

            HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String ip = in.readLine();
            in.close();
            return ip;
        } catch (IOException e) {
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TorIpChanger().setVisible(true);
        });
    }
}
