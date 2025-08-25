package org.guercifzone.Views;





import org.guercifzone.Models.Controller;
import org.guercifzone.Models.Host;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

public class MainWindow extends JFrame {
    private Controller controller;
    private JList<String> hostList;
    private DefaultListModel<String> listModel;
    private JButton refreshButton;
    private JButton attackButton;
    private JButton recoverButton;
    private JButton quitButton;
    private JLabel statusLabel;

    public MainWindow() {
        super("Net Cut - Network Controller");
        initializeComponents();
        setupLayout();
        setupEventHandlers();

        // Initialize controller
        controller = new Controller(10000); // 10 second attack interval
    }

    private void initializeComponents() {
        listModel = new DefaultListModel<>();
        hostList = new JList<>(listModel);
        hostList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        hostList.setCellRenderer(new HostListCellRenderer());

        refreshButton = new JButton("Refresh");
        attackButton = new JButton("Attack Selected");
        recoverButton = new JButton("Recover Selected");
        quitButton = new JButton("Quit");

        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
    }

    private void setupLayout() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Net Cut - Network Controller", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Host list with scroll pane
        JScrollPane scrollPane = new JScrollPane(hostList);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(refreshButton);
        buttonPanel.add(attackButton);
        buttonPanel.add(recoverButton);
        buttonPanel.add(quitButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Status bar
        mainPanel.add(statusLabel, BorderLayout.NORTH);

        add(mainPanel);
    }

    private void setupEventHandlers() {
        refreshButton.addActionListener(e -> refreshHosts());
        attackButton.addActionListener(e -> attackSelected());
        recoverButton.addActionListener(e -> recoverSelected());
        quitButton.addActionListener(e -> quitApplication());

        // Double-click to toggle attack/recover
        hostList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int index = hostList.locationToIndex(evt.getPoint());
                    if (index != -1) {
                        controller.action(index + 1); // Convert to 1-based index
                        refreshHosts();
                    }
                }
            }
        });
    }

    private void refreshHosts() {
        statusLabel.setText("Scanning network...");
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                controller.scan_targets();
                return null;
            }

            @Override
            protected void done() {
                updateHostList();
                statusLabel.setText("Scan completed at " + new java.util.Date());
            }
        };
        worker.execute();
    }

    private void updateHostList() {
        listModel.clear();
        java.util.Set<Host> hosts = controller.getHosts();

        for (Host host : hosts) {
            String status = host.is_cut() ? "[CUT] " : "[OK] ";
            listModel.addElement(status + host.get_ip() + " (" + host.get_mac() + ")");
        }
    }

    private void attackSelected() {
        int[] indices = hostList.getSelectedIndices();
        if (indices.length == 0) {
            JOptionPane.showMessageDialog(this, "Please select at least one host to attack");
            return;
        }

        statusLabel.setText("Attacking selected hosts...");
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (int index : indices) {
                    controller.action(index + 1); // Convert to 1-based index
                }
                return null;
            }

            @Override
            protected void done() {
                refreshHosts();
                statusLabel.setText("Attack completed at " + new java.util.Date());
            }
        };
        worker.execute();
    }

    private void recoverSelected() {
        int[] indices = hostList.getSelectedIndices();
        if (indices.length == 0) {
            JOptionPane.showMessageDialog(this, "Please select at least one host to recover");
            return;
        }

        statusLabel.setText("Recovering selected hosts...");
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (int index : indices) {
                    // For recovery, we need to check if the host is already cut
                    // and if so, perform recovery action
                    java.util.Set<Host> hosts = controller.getHosts();
                    int i = 0;
                    for (Host host : hosts) {
                        if (i == index && host.is_cut()) {
                            controller.action(index + 1); // Toggle status
                            break;
                        }
                        i++;
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                refreshHosts();
                statusLabel.setText("Recovery completed at " + new java.util.Date());
            }
        };
        worker.execute();
    }

    private void quitApplication() {
        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to quit? All attacks will be stopped.",
                "Confirm Quit", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            controller.recover_all_hosts();
            System.exit(0);
        }
    }

    // Custom cell renderer to color hosts based on status
    private class HostListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            java.util.Set<Host> hosts = controller.getHosts();
            int i = 0;
            for (Host host : hosts) {
                if (i == index) {
                    if (host.is_cut()) {
                        c.setForeground(Color.RED);
                    } else {
                        c.setForeground(Color.GREEN.darker());
                    }
                    break;
                }
                i++;
            }

            return c;
        }
    }
}
