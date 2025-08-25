package org.guercifzone.Utils;



import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class SocketUtils {

    public static int open_socket(String ip) throws IOException {
        // Create a UDP socket for sending ARP requests
        DatagramSocket socket = new DatagramSocket();
        socket.setReuseAddress(true);
        return socket.getLocalPort(); // Return port number (simplified)
    }

    public static void bind_socket(int socket, String interfaceName) throws SocketException {
        // In Java, we can't directly bind to a specific interface like in C++
        // This is a placeholder implementation
        System.out.println("Binding to interface: " + interfaceName);
    }

    public static void close_sockets() {
        // Clean up any open sockets
        // This would be more complex in a real implementation
        System.out.println("Closing all sockets");
    }

    public static int get_interface_index(int sd, String interfaceName) {
        try {
            NetworkInterface networkInterface = NetworkInterface.getByName(interfaceName);
            if (networkInterface != null) {
                return networkInterface.getIndex();
            }
        } catch (SocketException e) {
            System.err.println("Failed to get interface index: " + e.getMessage());
        }
        return -1;
    }

    public static String get_interface_mac_address(String interfaceName) {
        try {
            NetworkInterface networkInterface = NetworkInterface.getByName(interfaceName);
            if (networkInterface != null) {
                byte[] mac = networkInterface.getHardwareAddress();
                if (mac != null) {
                    return MacUtils.byte_mac_to_string(mac);
                }
            }
        } catch (SocketException e) {
            System.err.println("Failed to get interface MAC: " + e.getMessage());
        }
        return "00:00:00:00:00:00"; // Default MAC
    }
}
