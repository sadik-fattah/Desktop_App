package org.guercifzone.Models;


import java.net.InetAddress;
import java.net.UnknownHostException;

public class Host implements Comparable<Host> {
    private String ip_address;
    private String mac_address;
    private Status status;

    public Host(String ip_address, String mac_address) {
        this.ip_address = ip_address;
        this.mac_address = mac_address;
        this.status = Status.NORMAL;
    }

    @Override
    public int compareTo(Host other) {
        try {
            InetAddress self_ip = InetAddress.getByName(ip_address);
            InetAddress other_ip = InetAddress.getByName(other.ip_address);
            byte[] self_bytes = self_ip.getAddress();
            byte[] other_bytes = other_ip.getAddress();

            for (int i = 0; i < self_bytes.length; i++) {
                int self_byte = self_bytes[i] & 0xFF;
                int other_byte = other_bytes[i] & 0xFF;
                if (self_byte != other_byte) {
                    return self_byte - other_byte;
                }
            }
            return 0;
        } catch (UnknownHostException e) {
            return ip_address.compareTo(other.ip_address);
        }
    }

    public String get_ip() {
        return ip_address;
    }

    public String get_mac() {
        return mac_address;
    }

    public boolean is_cut() {
        return status == Status.CUT;
    }

    public void set_status(Status status) {
        this.status = status;
    }

    public void set_mac_address(String mac_address) {
        this.mac_address = mac_address;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Host host = (Host) obj;
        return ip_address.equals(host.ip_address);
    }

    @Override
    public int hashCode() {
        return ip_address.hashCode();
    }
}