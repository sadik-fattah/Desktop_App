package org.guercifzone.Utils;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadUtils {
    public static Map<String, Boolean> listen_signal = new ConcurrentHashMap<>();
    public static Map<String, Thread> listening_thread = new ConcurrentHashMap<>();
    public static Map<String, Map<String, Boolean>> spoof_signal = new ConcurrentHashMap<>();
    public static Map<String, Map<String, Thread>> spoof_thread = new ConcurrentHashMap<>();

    public static boolean stop_thread(String target_ip, String spoof_src_ip) {
        if (spoof_signal.containsKey(target_ip) &&
                spoof_signal.get(target_ip).containsKey(spoof_src_ip)) {
            spoof_signal.get(target_ip).put(spoof_src_ip, false);

            if (spoof_thread.containsKey(target_ip) &&
                    spoof_thread.get(target_ip).containsKey(spoof_src_ip)) {
                Thread thread = spoof_thread.get(target_ip).get(spoof_src_ip);
                if (thread != null && thread.isAlive()) {
                    thread.interrupt();
                    try {
                        thread.join(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                spoof_thread.get(target_ip).remove(spoof_src_ip);
            }

            return true;
        }
        return false;
    }

    public static void stop_all_threads() {
        // Stop all listening threads
        for (Map.Entry<String, Boolean> entry : listen_signal.entrySet()) {
            entry.setValue(false);
        }

        for (Map.Entry<String, Thread> entry : listening_thread.entrySet()) {
            if (entry.getValue() != null && entry.getValue().isAlive()) {
                entry.getValue().interrupt();
            }
        }

        // Stop all spoofing threads
        for (Map.Entry<String, Map<String, Boolean>> outer : spoof_signal.entrySet()) {
            for (Map.Entry<String, Boolean> inner : outer.getValue().entrySet()) {
                inner.setValue(false);
            }
        }

        for (Map.Entry<String, Map<String, Thread>> outer : spoof_thread.entrySet()) {
            for (Map.Entry<String, Thread> inner : outer.getValue().entrySet()) {
                if (inner.getValue() != null && inner.getValue().isAlive()) {
                    inner.getValue().interrupt();
                }
            }
        }

        // Clear all maps
        listen_signal.clear();
        listening_thread.clear();
        spoof_signal.clear();
        spoof_thread.clear();
    }
}
