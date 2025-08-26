package org.Guercifzone.IPAdress;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.regex.Pattern;

public class GetMyIpAddress {
    private static final Pattern PATTERN = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
    String[] ipAddresses = new String[10];
    String temp;
    int j = 0;

    public static boolean validateIP(String ip) {
        return PATTERN.matcher(ip).matches();
    }

    public boolean validatePort(String portNumber) {
        if (portNumber != null && portNumber.length() >= 4 && portNumber.matches(".*\\d.*")) {
            return Integer.parseInt(portNumber) > 1023;
        } else {
            return false;
        }
    }

    public String[] ipAddress() {
        System.out.println("Printing only IPv4 Addresses");
        Enumeration e = null;
        String s = "";

        try {
            e = NetworkInterface.getNetworkInterfaces();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        while(e.hasMoreElements()) {
            NetworkInterface n = (NetworkInterface)e.nextElement();
            Enumeration ee = n.getInetAddresses();

            while(ee.hasMoreElements()) {
                InetAddress i = (InetAddress)ee.nextElement();
                this.temp = i.getHostAddress();
                if ((this.temp.charAt(1) == '7' || this.temp.charAt(1) == '9') && this.temp.charAt(2) == '2') {
                    this.ipAddresses[this.j] = this.temp;
                    ++this.j;
                    System.out.println(this.temp);
                } else if (this.temp.charAt(0) == '1' && this.temp.charAt(1) == '0') {
                    this.ipAddresses[this.j] = this.temp;
                    ++this.j;
                    System.out.println(this.temp);
                }
            }
        }

        if (this.ipAddresses[0] == null) {
            this.ipAddresses[0] = "127.0.0.1";
        }

        return this.ipAddresses;
    }

    public static void main(String[] args) {
        (new GetMyIpAddress()).ipAddress();
    }
}
