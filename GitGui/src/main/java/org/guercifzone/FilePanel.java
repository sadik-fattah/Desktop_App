package org.guercifzone;



import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class FilePanel extends JPanel {
    private JList<String> fileList;
    private DefaultListModel<String> listModel;
    private JButton stageButton;
    private JButton unstageButton;
    private JButton refreshButton;
    private GitOperations gitOps;
    private String currentDirectory;

    public FilePanel() {
        gitOps = new GitOperations();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("File Status"));
        setPreferredSize(new Dimension(600, 300));

        // List model for files
        listModel = new DefaultListModel<>();
        fileList = new JList<>(listModel);
        fileList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        fileList.setCellRenderer(new FileListRenderer());

        JScrollPane scrollPane = new JScrollPane(fileList);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        stageButton = new JButton("Stage Selected");
        stageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stageSelectedFiles();
            }
        });

        unstageButton = new JButton("Unstage Selected");
        unstageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                unstageSelectedFiles();
            }
        });

        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshFileList();
            }
        });

        buttonPanel.add(stageButton);
        buttonPanel.add(unstageButton);
        buttonPanel.add(refreshButton);

        // Add selection listener to enable/disable buttons
        fileList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                updateButtonStates();
            }
        });

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        updateButtonStates();
    }

    public void setDirectory(String directory) {
        this.currentDirectory = directory;
        refreshFileList();
    }

    public void refreshFileList() {
        if (currentDirectory == null || currentDirectory.isEmpty()) {
            listModel.clear();
            listModel.addElement("Please select a Git repository directory");
            return;
        }

        listModel.clear();
        try {
            // Check if directory exists
            if (!gitOps.directoryExists(currentDirectory)) {
                listModel.addElement("Directory does not exist: " + currentDirectory);
                return;
            }

            // Check if it's a git repository
            if (!gitOps.isGitRepository(currentDirectory)) {
                listModel.addElement("Not a Git repository: " + currentDirectory);
                return;
            }

            // Get status with porcelain format for easy parsing
            String statusOutput = gitOps.executeGitCommand(currentDirectory, "status --porcelain");
            String[] lines = statusOutput.split("\n");

            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    listModel.addElement(line);
                }
            }

            if (listModel.isEmpty()) {
                listModel.addElement("No changes detected - working tree clean");
            }
        } catch (Exception e) {
            listModel.addElement("Error: " + e.getMessage());
        }
    }

    private void stageSelectedFiles() {
        List<String> selectedFiles = getSelectedFilenames();
        if (selectedFiles.isEmpty()) return;

        try {
            for (String filename : selectedFiles) {
                gitOps.executeGitCommand(currentDirectory, "add \"" + filename + "\"");
            }
            JOptionPane.showMessageDialog(this, "Staged " + selectedFiles.size() + " file(s)");
            refreshFileList();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error staging files: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void unstageSelectedFiles() {
        List<String> selectedFiles = getSelectedFilenames();
        if (selectedFiles.isEmpty()) return;

        try {
            for (String filename : selectedFiles) {
                gitOps.executeGitCommand(currentDirectory, "restore --staged \"" + filename + "\"");
            }
            JOptionPane.showMessageDialog(this, "Unstaged " + selectedFiles.size() + " file(s)");
            refreshFileList();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error unstaging files: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<String> getSelectedFilenames() {
        List<String> selectedFiles = new ArrayList<>();
        for (String item : fileList.getSelectedValuesList()) {
            // Parse filename from status line (format: "XY filename")
            if (item.length() > 3) {
                String filename = item.substring(3).trim();
                // Remove quotes if present
                if (filename.startsWith("\"") && filename.endsWith("\"")) {
                    filename = filename.substring(1, filename.length() - 1);
                }
                selectedFiles.add(filename);
            }
        }
        return selectedFiles;
    }

    private void updateButtonStates() {
        boolean hasSelection = !fileList.getSelectedValuesList().isEmpty();
        boolean hasFiles = !listModel.isEmpty() &&
                !listModel.get(0).equals("No changes detected - working tree clean") &&
                !listModel.get(0).equals("Please select a Git repository directory") &&
                !listModel.get(0).startsWith("Not a Git repository") &&
                !listModel.get(0).startsWith("Directory does not exist");

        stageButton.setEnabled(hasSelection && hasFiles);
        unstageButton.setEnabled(hasSelection && hasFiles);
        refreshButton.setEnabled(currentDirectory != null && !currentDirectory.isEmpty());
    }

    // Custom renderer to color code file status
    private class FileListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {

            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

            if (value instanceof String) {
                String status = (String) value;

                // Color coding based on git status
                if (status.startsWith("??")) {
                    label.setForeground(Color.BLUE); // Untracked files
                    label.setText("Untracked: " + status.substring(3));
                } else if (status.startsWith("M ") || status.startsWith(" M")) {
                    label.setForeground(Color.GREEN.darker()); // Modified
                    label.setText("Modified: " + status.substring(2).trim());
                } else if (status.startsWith("A ") || status.startsWith(" A")) {
                    label.setForeground(Color.GREEN); // Added
                    label.setText("Added: " + status.substring(2).trim());
                } else if (status.startsWith("D ") || status.startsWith(" D")) {
                    label.setForeground(Color.RED); // Deleted
                    label.setText("Deleted: " + status.substring(2).trim());
                } else if (status.startsWith("R ") || status.startsWith(" R")) {
                    label.setForeground(Color.ORANGE); // Renamed
                    label.setText("Renamed: " + status.substring(2).trim());
                } else {
                    label.setForeground(Color.BLACK); // Default
                    label.setText(status);
                }
            }

            return label;
        }
    }
}