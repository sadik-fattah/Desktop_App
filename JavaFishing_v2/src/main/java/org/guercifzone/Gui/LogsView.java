package org.guercifzone.Gui;


import org.guercifzone.DataBase.DatabaseHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;


public class LogsView extends JFrame {
    private JTable logsTable;
    private DatabaseHelper dbHelper;

    public LogsView() {
        dbHelper = new DatabaseHelper();
        initializeUI();
        loadLogs();
    }

    private void initializeUI() {
        setTitle("Login Logs Viewer");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create components
        logsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(logsTable);
        JButton refreshBtn = new JButton("Refresh Logs");
        JButton exportBtn = new JButton("Export to CSV");

        // Button actions
        refreshBtn.addActionListener(e -> loadLogs());
        exportBtn.addActionListener(e -> exportToCSV());

        // Layout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(refreshBtn);
        buttonPanel.add(exportBtn);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(buttonPanel, BorderLayout.NORTH);
        contentPane.add(scrollPane, BorderLayout.CENTER);
    }

    private void loadLogs() {
        String[] columnNames = {"Email", "Password", "IP Address", "Last Login"};
        String[][] data = dbHelper.getLogsAsArray();

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        logsTable.setModel(model);
        logsTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Password column
        logsTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Timestamp column
    }

    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Logs as CSV");
        fileChooser.setSelectedFile(new java.io.File("login_logs.csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = fileChooser.getSelectedFile();
                dbHelper.exportToCSV(file.getAbsolutePath());
                JOptionPane.showMessageDialog(this,
                        "Logs exported successfully to:\n" + file.getAbsolutePath(),
                        "Export Successful",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error exporting logs: " + ex.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}