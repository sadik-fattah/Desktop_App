package org.guercifzone.Models;



import java.net.InetAddress;
import java.net.UnknownHostException;

public class Interface {
    private String name;
    private String ip;
    private String netmask;

    public Interface(String name, String ip, String netmask) {
        this.name = name;
        this.ip = ip;
        this.netmask = netmask;
    }

    public Interface() {
        // Empty interface
    }

    public String get_name() {
        return name;
    }

    public String get_ip() {
        return ip;
    }

    public String get_netmask() {
        return netmask;
    }

    public boolean is_same_subnet(String ip) {
        try {
            InetAddress interface_ip = InetAddress.getByName(this.ip);
            InetAddress interface_netmask = InetAddress.getByName(this.netmask);
            InetAddress check_ip = InetAddress.getByName(ip);

            byte[] interface_bytes = interface_ip.getAddress();
            byte[] netmask_bytes = interface_netmask.getAddress();
            byte[] check_bytes = check_ip.getAddress();

            if (interface_bytes.length != netmask_bytes.length ||
                    interface_bytes.length != check_bytes.length) {
                return false;
            }

            for (int i = 0; i < interface_bytes.length; i++) {
                int interface_byte = interface_bytes[i] & 0xFF;
                int netmask_byte = netmask_bytes[i] & 0xFF;
                int check_byte = check_bytes[i] & 0xFF;

                if ((interface_byte & netmask_byte) != (check_byte & netmask_byte)) {
                    return false;
                }
            }

            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }
}
