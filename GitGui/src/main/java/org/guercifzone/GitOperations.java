package org.guercifzone;


import java.io.BufferedReader;
import java.io.InputStreamReader;

public class GitOperations {

    public String executeGitCommand(String directory, String command) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder();

        // Use bash for Linux to handle the command properly
        String[] cmd = {"bash", "-c", "git " + command};

        processBuilder.command(cmd);
        processBuilder.directory(new java.io.File(directory));

        Process process = processBuilder.start();

        // Read output stream
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            // Read error stream
            StringBuilder errorOutput = new StringBuilder();
            try (BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = errorReader.readLine()) != null) {
                    errorOutput.append(line).append("\n");
                }
            }
            throw new RuntimeException("Git command failed (" + exitCode + "): " +
                    errorOutput.toString() + "\nCommand: git " + command);
        }

        return output.toString();
    }

    public boolean isGitRepository(String directory) {
        try {
            executeGitCommand(directory, "rev-parse --is-inside-work-tree");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Check if remote uses SSH or HTTPS
    public String getRemoteUrl(String directory) throws Exception {
        try {
            return executeGitCommand(directory, "config --get remote.origin.url").trim();
        } catch (Exception e) {
            return "No remote origin configured";
        }
    }

    // Change remote URL to SSH
    public void setRemoteToSSH(String directory, String githubUsername, String repoName) throws Exception {
        String sshUrl = "git@github.com:" + githubUsername + "/" + repoName + ".git";
        executeGitCommand(directory, "remote set-url origin " + sshUrl);
    }

    // Check if SSH is configured properly
    public boolean isSSHConfigured() {
        try {
            // Test SSH connection to GitHub
            Process process = new ProcessBuilder("ssh", "-T", "git@github.com").start();
            int exitCode = process.waitFor();
            // SSH returns 1 on successful connection with "successfully authenticated" message
            return exitCode == 1;
        } catch (Exception e) {
            return false;
        }
    }

    // Generate SSH key if not exists
    public void generateSSHKey(String email) throws Exception {
        // Check if key already exists
        java.io.File sshKey = new java.io.File(System.getProperty("user.home") + "/.ssh/id_ed25519");
        if (!sshKey.exists()) {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "ssh-keygen", "-t", "ed25519", "-C", email, "-N", "", "-f",
                    System.getProperty("user.home") + "/.ssh/id_ed25519"
            );
            Process process = processBuilder.start();
            process.waitFor();
        }
    }

    // Get SSH public key
    public String getSSHPublicKey() throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder("cat",
                System.getProperty("user.home") + "/.ssh/id_ed25519.pub");
        Process process = processBuilder.start();

        StringBuilder key = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                key.append(line).append("\n");
            }
        }

        process.waitFor();
        return key.toString().trim();
    }

    // Add SSH key to ssh-agent
    public void addSSHKeyToAgent() throws Exception {
        // Start ssh-agent if not running
        ProcessBuilder agentBuilder = new ProcessBuilder("ssh-add", "-l");
        Process agentProcess = agentBuilder.start();
        int agentStatus = agentProcess.waitFor();

        if (agentStatus != 0) {
            // Start ssh-agent
            ProcessBuilder evalBuilder = new ProcessBuilder("bash", "-c", "eval \"$(ssh-agent -s)\"");
            evalBuilder.start().waitFor();
        }

        // Add the key
        ProcessBuilder addBuilder = new ProcessBuilder("ssh-add",
                System.getProperty("user.home") + "/.ssh/id_ed25519");
        addBuilder.start().waitFor();
    }

    // Check if directory exists
    public boolean directoryExists(String directory) {
        return new java.io.File(directory).exists();
    }
}