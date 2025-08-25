package org.guercifzone.Models;



import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NetworkScanner {
    private Map<String, String> arp_table;
    private List<Interface> interfaces;

    public NetworkScanner() {
        this.arp_table = new ConcurrentHashMap<>();
        this.interfaces = new ArrayList<>();
    }

    public List<Host> scan_networks() {
        // Clear previous results
        arp_table.clear();
        interfaces.clear();

        try {
            // Get all network interfaces
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();

                // Skip loopback and down interfaces
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }

                // Get interface addresses
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();

                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();

                    // Only process IPv4 addresses
                    if (address.getAddress().length == 4) { // IPv4
                        String interfaceName = networkInterface.getName();
                        String ip = address.getHostAddress();

                        // Calculate netmask from network prefix length
                        String netmask = calculateNetmask(networkInterface, address);

                        if (netmask != null) {
                            Interface iface = new Interface(interfaceName, ip, netmask);
                            interfaces.add(iface);

                            // Read existing ARP table entries for this interface
                            readArpTable(iface);
                        }
                    }
                }
            }
        } catch (SocketException e) {
            System.err.println("Failed to scan networks: " + e.getMessage());
        }

        // Convert arp_table to Host objects
        List<Host> hosts = new ArrayList<>();
        for (Map.Entry<String, String> entry : arp_table.entrySet()) {
            hosts.add(new Host(entry.getKey(), entry.getValue()));
        }

        return hosts;
    }

    private String calculateNetmask(NetworkInterface networkInterface, InetAddress address) {
        try {
            // Get network prefix length (subnet mask in CIDR notation)
            short prefixLength = networkInterface.getInterfaceAddresses().stream()
                    .filter(ifaceAddr -> ifaceAddr.getAddress().equals(address))
                    .map(ifaceAddr -> ifaceAddr.getNetworkPrefixLength())
                    .findFirst()
                    .orElse((short)24); // Default to /24 if not found

            // Convert prefix length to subnet mask
            int mask = 0xffffffff << (32 - prefixLength);
            byte[] netmaskBytes = new byte[] {
                    (byte)(mask >>> 24),
                    (byte)(mask >>> 16),
                    (byte)(mask >>> 8),
                    (byte)mask
            };

            return InetAddress.getByAddress(netmaskBytes).getHostAddress();
        } catch (Exception e) {
            return "255.255.255.0"; // Fallback to default subnet mask
        }
    }

    private void readArpTable(Interface interface_) {
        try {
            // Get the network interface
            NetworkInterface networkInterface = NetworkInterface.getByName(interface_.get_name());
            if (networkInterface == null) {
                return;
            }

            // Get the system's ARP table by reading /proc/net/arp (Linux) or using system command
            if (System.getProperty("os.name").toLowerCase().contains("linux")) {
                readLinuxArpTable();
            } else if (System.getProperty("os.name").toLowerCase().contains("win")) {
                readWindowsArpTable();
            } else if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                readMacArpTable();
            }

        } catch (Exception e) {
            System.err.println("Failed to read ARP table: " + e.getMessage());
        }
    }

    private void readLinuxArpTable() {
        try {
            // Read /proc/net/arp file
            java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.FileReader("/proc/net/arp"));

            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false; // Skip header
                    continue;
                }

                String[] parts = line.split("\\s+");
                if (parts.length >= 4) {
                    String ip = parts[0];
                    String mac = parts[3];

                    // Validate MAC address format
                    if (mac.matches("([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})") &&
                            !mac.equals("00:00:00:00:00:00")) {
                        arp_table.put(ip, mac);
                    }
                }
            }

            reader.close();

        } catch (Exception e) {
            System.err.println("Failed to read Linux ARP table: " + e.getMessage());
            // Fallback to system command
            readArpTableSystemCommand();
        }
    }

    private void readWindowsArpTable() {
        try {
            Process process = Runtime.getRuntime().exec("arp -a");
            java.util.Scanner scanner = new java.util.Scanner(process.getInputStream());

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                // Parse ARP table entries (Windows format)
                if (line.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\s+\\S+\\s+\\S+")) {
                    String[] parts = line.split("\\s+");
                    if (parts.length >= 3) {
                        String ip = parts[0];
                        String mac = parts[1].replace("-", ":");

                        // Validate MAC address format
                        if (mac.matches("([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})") &&
                                !mac.equals("00:00:00:00:00:00")) {
                            arp_table.put(ip, mac);
                        }
                    }
                }
            }

            scanner.close();
            process.waitFor();

        } catch (Exception e) {
            System.err.println("Failed to read Windows ARP table: " + e.getMessage());
        }
    }

    private void readMacArpTable() {
        try {
            Process process = Runtime.getRuntime().exec("arp -a");
            java.util.Scanner scanner = new java.util.Scanner(process.getInputStream());

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                // Parse ARP table entries (macOS format)
                if (line.matches("\\S+\\.local\\s+\\(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\)\\s+\\S+\\s+\\S+") ||
                        line.matches("\\?\\s+\\(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\)\\s+\\S+\\s+\\S+")) {

                    String[] parts = line.split("\\s+");
                    if (parts.length >= 4) {
                        // Extract IP from format like "hostname.local (192.168.1.1)" or "? (192.168.1.1)"
                        String ip = parts[1].replaceAll("[()]", "");
                        String mac = parts[3];

                        // Validate MAC address format
                        if (mac.matches("([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})") &&
                                !mac.equals("00:00:00:00:00:00")) {
                            arp_table.put(ip, mac);
                        }
                    }
                }
            }

            scanner.close();
            process.waitFor();

        } catch (Exception e) {
            System.err.println("Failed to read macOS ARP table: " + e.getMessage());
        }
    }

    private void readArpTableSystemCommand() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String command;

            if (os.contains("win")) {
                command = "arp -a";
            } else {
                command = "arp -n";
            }

            Process process = Runtime.getRuntime().exec(command);
            java.util.Scanner scanner = new java.util.Scanner(process.getInputStream());

            if (os.contains("win")) {
                // Windows format parsing
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (line.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\s+\\S+\\s+\\S+")) {
                        String[] parts = line.split("\\s+");
                        if (parts.length >= 3) {
                            String ip = parts[0];
                            String mac = parts[1].replace("-", ":");
                            if (isValidMac(mac)) {
                                arp_table.put(ip, mac);
                            }
                        }
                    }
                }
            } else {
                // Unix format parsing
                boolean firstLine = true;
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (firstLine) {
                        firstLine = false;
                        continue;
                    }
                    if (line.matches("\\S+\\s+\\(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\)\\s+\\S+\\s+\\S+") ||
                            line.matches("\\?\\s+\\(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\)\\s+\\S+\\s+\\S+")) {
                        String[] parts = line.split("\\s+");
                        if (parts.length >= 4) {
                            String ip = parts[1].replaceAll("[()]", "");
                            String mac = parts[3];
                            if (isValidMac(mac)) {
                                arp_table.put(ip, mac);
                            }
                        }
                    }
                }
            }

            scanner.close();
            process.waitFor();

        } catch (Exception e) {
            System.err.println("Failed to read ARP table via system command: " + e.getMessage());
        }
    }

    private boolean isValidMac(String mac) {
        return mac.matches("([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})") &&
                !mac.equals("00:00:00:00:00:00") &&
                !mac.equals("ff:ff:ff:ff:ff:ff");
    }

    public Interface get_interface_by_ip(String ip) {
        for (Interface iface : interfaces) {
            if (iface.is_same_subnet(ip)) {
                return iface;
            }
        }
        return new Interface(); // Return empty interface if not found
    }

    // Helper method to convert byte array to integer
    private int byteArrayToInt(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 24) |
                ((bytes[1] & 0xFF) << 16) |
                ((bytes[2] & 0xFF) << 8) |
                (bytes[3] & 0xFF);
    }

    // Helper method to convert integer to IP address string
    private String intToIpAddress(int ip) {
        return ((ip >> 24) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                (ip & 0xFF);
    }
}