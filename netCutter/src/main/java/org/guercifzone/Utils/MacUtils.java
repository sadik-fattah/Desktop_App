package org.guercifzone.Utils;



import java.util.Random;

public class MacUtils {
    private static final Random random = new Random();

    public static String get_random_mac_address() {
        StringBuilder mac = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            mac.append(String.format("%02X", random.nextInt(256)));
            if (i < 5) mac.append(":");
        }
        return mac.toString();
    }

    public static String byte_mac_to_string(byte[] mac) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X", mac[i]));
            if (i < mac.length - 1) sb.append(":");
        }
        return sb.toString();
    }

    public static byte[] string_mac_to_byte(String mac) {
        String[] hex = mac.split(":");
        byte[] bytes = new byte[hex.length];
        for (int i = 0; i < hex.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex[i], 16);
        }
        return bytes;
    }
}
