package org.guercifzone;



import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GitGUI extends JFrame {
    private GitOperations gitOps;
    private JTextArea outputArea;
    private JTextField directoryField;
    private JTextField commitMessageField;
    private FilePanel filePanel;
    private JTabbedPane tabbedPane;

    public GitGUI() {
        gitOps = new GitOperations();
        initializeUI();
        setupSSHForLinux();
    }

    private void initializeUI() {
        setTitle("Git GUI Manager - Linux");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Top panel - Directory selection
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Repository Directory:"));
        directoryField = new JTextField(35);
        directoryField.setText(System.getProperty("user.home"));
        topPanel.add(directoryField);

        JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                browseDirectory();
            }
        });
        topPanel.add(browseButton);

        // Button panel for operations
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton statusButton = new JButton("Status");
        statusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeGitCommand("status");
            }
        });

        JButton addButton = new JButton("Add All");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeGitCommand("add .");
            }
        });

        JButton commitButton = new JButton("Commit");
        commitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCommitDialog();
            }
        });

        JButton pushButton = new JButton("Push");
        pushButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handlePushOperation();
            }
        });

        JButton pullButton = new JButton("Pull");
        pullButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeGitCommand("pull origin main");
            }
        });

        JButton logButton = new JButton("Log");
        logButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeGitCommand("log --oneline -10");
            }
        });

        JButton branchButton = new JButton("Branches");
        branchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeGitCommand("branch -a");
            }
        });

        buttonPanel.add(statusButton);
        buttonPanel.add(addButton);
        buttonPanel.add(commitButton);
        buttonPanel.add(pushButton);
        buttonPanel.add(pullButton);
        buttonPanel.add(logButton);
        buttonPanel.add(branchButton);

        // Commit message panel
        JPanel commitPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        commitPanel.add(new JLabel("Commit Message:"));
        commitMessageField = new JTextField(30);
        commitPanel.add(commitMessageField);

        // Create file panel
        filePanel = new FilePanel();

        // Create tabbed pane
        tabbedPane = new JTabbedPane();

        // Operations tab
        JPanel operationsPanel = new JPanel(new BorderLayout());
        operationsPanel.add(buttonPanel, BorderLayout.NORTH);
        operationsPanel.add(commitPanel, BorderLayout.CENTER);

        // File status tab
        JPanel fileStatusPanel = new JPanel(new BorderLayout());
        fileStatusPanel.add(topPanel, BorderLayout.NORTH);
        fileStatusPanel.add(filePanel, BorderLayout.CENTER);

        tabbedPane.addTab("Git Operations", operationsPanel);
        tabbedPane.addTab("File Management", fileStatusPanel);

        // Output area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setPreferredSize(new Dimension(800, 150));

        // Add components to main panel
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void browseDirectory() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setCurrentDirectory(new java.io.File(System.getProperty("user.home")));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String selectedDir = fileChooser.getSelectedFile().getAbsolutePath();
            directoryField.setText(selectedDir);
            filePanel.setDirectory(selectedDir);
        }
    }

    private void executeGitCommand(String command) {
        String directory = directoryField.getText();
        if (directory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a directory");
            return;
        }

        try {
            String output = gitOps.executeGitCommand(directory, command);
            outputArea.setText("$ git " + command + "\n" + output);
            filePanel.refreshFileList(); // Refresh file list after operation
        } catch (Exception ex) {
            outputArea.setText("Error executing 'git " + command + "':\n" + ex.getMessage());
        }
    }

    private void showCommitDialog() {
        String message = commitMessageField.getText();
        if (message.isEmpty()) {
            message = JOptionPane.showInputDialog(this, "Enter commit message:");
            if (message == null || message.isEmpty()) return;
        }

        try {
            String output = gitOps.executeGitCommand(directoryField.getText(), "commit -m \"" + message + "\"");
            outputArea.setText("$ git commit -m \"" + message + "\"\n" + output);
            commitMessageField.setText(""); // Clear message field
            filePanel.refreshFileList(); // Refresh file list after commit
        } catch (Exception ex) {
            outputArea.setText("Error committing: " + ex.getMessage());
        }
    }

    private void handlePushOperation() {
        String directory = directoryField.getText();
        if (directory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a directory");
            return;
        }

        try {
            // Check if remote uses HTTPS (which requires authentication)
            String remoteUrl = gitOps.getRemoteUrl(directory);
            if (remoteUrl.startsWith("https://")) {
                int choice = JOptionPane.showOptionDialog(this,
                        "HTTPS remote detected. Would you like to switch to SSH (recommended) or try pushing anyway?",
                        "Authentication Required",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new String[]{"Switch to SSH", "Try Push Anyway"},
                        "Switch to SSH");

                if (choice == JOptionPane.YES_OPTION) {
                    // Switch to SSH
                    switchToSSH(directory);
                }
            }
            // Try to push
            executeGitCommand("push -u origin main");
        } catch (Exception ex) {
            outputArea.setText("Push error: " + ex.getMessage());
        }
    }

    private void switchToSSH(String directory) {
        String username = JOptionPane.showInputDialog(this, "Enter your GitHub username:");
        if (username == null || username.isEmpty()) return;

        // Extract repo name from HTTPS URL or ask for it
        try {
            String remoteUrl = gitOps.getRemoteUrl(directory);
            String repoName;
            if (remoteUrl.contains("/")) {
                String[] parts = remoteUrl.split("/");
                repoName = parts[parts.length - 1].replace(".git", "");
            } else {
                repoName = JOptionPane.showInputDialog(this, "Enter repository name:");
                if (repoName == null || repoName.isEmpty()) return;
            }

            gitOps.setRemoteToSSH(directory, username, repoName);
            outputArea.setText("Switched remote to SSH: git@github.com:" + username + "/" + repoName + ".git\n");
        } catch (Exception ex) {
            outputArea.setText("Error switching to SSH: " + ex.getMessage());
        }
    }

    private void setupSSHForLinux() {
        try {
            if (!gitOps.isSSHConfigured()) {
                int choice = JOptionPane.showOptionDialog(this,
                        "SSH is not configured for GitHub. Would you like to set it up?",
                        "SSH Setup",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new String[]{"Setup SSH", "Skip"},
                        "Setup SSH");

                if (choice == JOptionPane.YES_OPTION) {
                    String email = JOptionPane.showInputDialog(this,
                            "Enter your email for SSH key generation:");
                    if (email != null && !email.isEmpty()) {
                        gitOps.generateSSHKey(email);
                        gitOps.addSSHKeyToAgent();

                        String publicKey = gitOps.getSSHPublicKey();

                        // Show the public key to user
                        JTextArea keyArea = new JTextArea(publicKey, 5, 50);
                        keyArea.setEditable(false);
                        keyArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                        JScrollPane scrollPane = new JScrollPane(keyArea);

                        JOptionPane.showMessageDialog(this, scrollPane,
                                "Add this SSH Key to GitHub:\n" +
                                        "1. Go to GitHub → Settings → SSH and GPG keys\n" +
                                        "2. Click 'New SSH key'\n" +
                                        "3. Paste the key above\n" +
                                        "4. Click 'Add SSH key'",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        } catch (Exception ex) {
            outputArea.setText("SSH setup error: " + ex.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                GitGUI gui = new GitGUI();
                gui.setVisible(true);
            }
        });
    }
}