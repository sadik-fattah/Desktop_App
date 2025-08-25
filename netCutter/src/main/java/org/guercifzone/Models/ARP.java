package org.guercifzone.Models;


import org.guercifzone.Utils.ThreadUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ARP {
    private Interface interface_;

    public ARP(Interface interface_) {
        this.interface_ = interface_;
    }

    public ARP() {
        // Empty constructor
    }

    public void request(String target_ip) {
        // In pure Java, we can't send raw ARP packets without native libraries
        // This is a placeholder that just logs the request
        System.out.println("ARP request for: " + target_ip + " (simulated)");
    }

    public void listen(Map<String, String> arp_table) {
        // ARP listening is not implemented in pure Java
        // This would require JNI or external libraries
        System.out.println("ARP listening started for interface: " + interface_.get_name());

        ThreadUtils.listen_signal.put(interface_.get_ip(), true);
        Thread listening_thread = new Thread(() -> {
            while (ThreadUtils.listen_signal.getOrDefault(interface_.get_ip(), false)) {
                try {
                    Thread.sleep(1000); // Simulate listening
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        ThreadUtils.listening_thread.put(interface_.get_ip(), listening_thread);
        listening_thread.start();
    }

    public void spoof(Host target, Host spoof_src, int attack_interval_ms, String fake_mac_address) {
        String target_ip = target.get_ip();
        String spoof_src_ip = spoof_src.get_ip();

        ThreadUtils.spoof_signal.putIfAbsent(target_ip, new ConcurrentHashMap<>());
        ThreadUtils.spoof_signal.get(target_ip).put(spoof_src_ip, true);

        Thread spoof_thread = new Thread(() -> {
            long lastAttackTime = 0;

            while (ThreadUtils.spoof_signal.get(target_ip).getOrDefault(spoof_src_ip, false)) {
                long currentTime = System.currentTimeMillis();

                if (currentTime - lastAttackTime >= attack_interval_ms) {
                    System.out.println("ARP spoofing: Telling " + target_ip +
                            " that " + spoof_src_ip + " is at " + fake_mac_address);
                    lastAttackTime = currentTime;
                }

                try {
                    Thread.sleep(100); // Small delay to prevent CPU overload
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        ThreadUtils.spoof_thread.putIfAbsent(target_ip, new ConcurrentHashMap<>());
        ThreadUtils.spoof_thread.get(target_ip).put(spoof_src_ip, spoof_thread);
        spoof_thread.start();
    }

    public void recover(Host target, Host spoof_src) {
        String target_ip = target.get_ip();
        String spoof_src_ip = spoof_src.get_ip();

        if (ThreadUtils.stop_thread(target_ip, spoof_src_ip)) {
            System.out.println("ARP recovery: Restoring correct ARP entry for " +
                    target_ip + " -> " + spoof_src.get_mac());
        }
    }
}