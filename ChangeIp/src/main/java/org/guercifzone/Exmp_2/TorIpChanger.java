package org.guercifzone.Exmp_2;

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

public class TorIpChanger {

    private static final String TOR_PROXY_HOST = "127.0.0.1";
    private static final int TOR_PROXY_PORT = 9050;
    private static final int TOR_CONTROL_PORT = 9051;
    private static final String TOR_CONTROL_PASSWORD = "your_password"; // Set in torrc

    public static void main(String[] args) {
        try {
            // Check if Tor is running
            if (!isTorRunning()) {
                System.out.println("Tor is not running. Please start Tor service.");
                return;
            }

            // Get current IP
            String originalIp = getCurrentIp();
            System.out.println("Original IP: " + originalIp);

            // Change Tor IP
            changeTorIp();

            // Get new IP
            String newIp = getCurrentIp();
            System.out.println("New IP: " + newIp);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isTorRunning() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(TOR_PROXY_HOST, TOR_PROXY_PORT), 1000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static String getCurrentIp() throws IOException {
        HttpHost torProxy = new HttpHost(TOR_PROXY_HOST, TOR_PROXY_PORT, "socks");
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

    private static void changeTorIp() throws IOException {
        // This requires authentication to Tor control port
        // You can use a library like TelnetClient from Apache Commons Net
        // or implement a simple socket connection

        // Note: This is a simplified version. In production, use proper authentication.
        try (Socket controlSocket = new Socket(TOR_PROXY_HOST, TOR_CONTROL_PORT)) {
            // Authenticate
            String authCommand = "AUTHENTICATE \"" + TOR_CONTROL_PASSWORD + "\"\r\n";
            controlSocket.getOutputStream().write(authCommand.getBytes());

            // Send NEWNYM signal
            String newnymCommand = "SIGNAL NEWNYM\r\n";
            controlSocket.getOutputStream().write(newnymCommand.getBytes());

            // Wait for the circuit to be rebuilt
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}