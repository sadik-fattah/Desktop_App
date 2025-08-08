package org.guercifzone.OneVideo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class YouTubeDownloader extends JFrame {
    private JTextField urlField;
    private JButton downloadButton;
    private JProgressBar progressBar;
    private JTextArea logArea;
    private JComboBox<String> qualityCombo;
    private JTextField outputFolderField;
    private JButton browseButton;

    public YouTubeDownloader() {
        setTitle("YouTube Video Downloader");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        urlField = new JTextField(30);
        downloadButton = new JButton("Download");
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        logArea = new JTextArea(10, 50);
        logArea.setEditable(false);

        String[] qualities = {"Highest Quality", "Medium Quality", "Low Quality"};
        qualityCombo = new JComboBox<>(qualities);

        outputFolderField = new JTextField(System.getProperty("user.home") + File.separator + "Downloads");
        browseButton = new JButton("Browse...");

        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> downloadVideo()).start();
            }
        });

        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseOutputFolder();
            }
        });
    }

    private void layoutComponents() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel with URL input
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("YouTube URL:"));
        topPanel.add(urlField);

        // Middle panel with options
        JPanel middlePanel = new JPanel(new GridLayout(2, 2, 5, 5));
        middlePanel.add(new JLabel("Video Quality:"));
        middlePanel.add(qualityCombo);
        middlePanel.add(new JLabel("Output Folder:"));

        JPanel folderPanel = new JPanel(new BorderLayout(5, 5));
        folderPanel.add(outputFolderField, BorderLayout.CENTER);
        folderPanel.add(browseButton, BorderLayout.EAST);
        middlePanel.add(folderPanel);

        // Bottom panel with download button and progress
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.add(downloadButton, BorderLayout.NORTH);
        bottomPanel.add(progressBar, BorderLayout.CENTER);

        // Add all panels to main panel
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(middlePanel, BorderLayout.CENTER);
        panel.add(new JScrollPane(logArea), BorderLayout.SOUTH);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        add(panel);
    }

    private void chooseOutputFolder() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setCurrentDirectory(new File(outputFolderField.getText()));

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            outputFolderField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void downloadVideo() {
        SwingUtilities.invokeLater(() -> {
            downloadButton.setEnabled(false);
            progressBar.setValue(0);
        });

        String youtubeUrl = urlField.getText().trim();
        if (youtubeUrl.isEmpty()) {
            appendLog("Please enter a YouTube URL");
            enableDownloadButton();
            return;
        }

        String outputFolder = outputFolderField.getText().trim();
        File folder = new File(outputFolder);
        if (!folder.exists() || !folder.isDirectory()) {
            appendLog("Invalid output folder");
            enableDownloadButton();
            return;
        }

        try {
            appendLog("Extracting video information...");
            String videoId = extractVideoId(youtubeUrl);
            if (videoId == null) {
                appendLog("Invalid YouTube URL");
                enableDownloadButton();
                return;
            }

            String infoUrl = "https://www.youtube.com/get_video_info?video_id=" + videoId +
                    "&el=embedded&ps=default&eurl=&gl=US&hl=en";
            appendLog("Fetching video info from: " + infoUrl);

            String videoInfo = fetchUrlContent(infoUrl);
            if (videoInfo == null || videoInfo.isEmpty()) {
                appendLog("Failed to fetch video information");
                enableDownloadButton();
                return;
            }

            Map<String, String> videoInfoMap = parseQueryString(videoInfo);
            String playerResponse = videoInfoMap.get("player_response");

            if (playerResponse == null || playerResponse.isEmpty()) {
                appendLog("Could not extract player response. YouTube may have changed their API.");
                appendLog("Try again or check if the video is available.");
                enableDownloadButton();
                return;
            }

            try {
                JsonObject playerResponseJson = JsonParser.parseString(playerResponse).getAsJsonObject();
                JsonObject videoDetails = playerResponseJson.getAsJsonObject("videoDetails");
                String title = videoDetails.get("title").getAsString();

                JsonObject streamingData = playerResponseJson.getAsJsonObject("streamingData");
                if (streamingData == null) {
                    appendLog("This video cannot be downloaded (may be age-restricted or private)");
                    enableDownloadButton();
                    return;
                }

                // Try to get adaptive formats first
                String downloadUrl = null;
                if (streamingData.has("adaptiveFormats")) {
                    appendLog("Found adaptive formats");
                    downloadUrl = getBestAdaptiveFormat(streamingData);
                }

                if (downloadUrl == null && streamingData.has("formats")) {
                    appendLog("Falling back to standard formats");
                    downloadUrl = getBestStandardFormat(streamingData);
                }

                if (downloadUrl == null) {
                    appendLog("No downloadable streams found");
                    enableDownloadButton();
                    return;
                }

                appendLog("Found video: " + title);
                appendLog("Starting download...");

                downloadFile(downloadUrl, outputFolder, title);

            } catch (Exception e) {
                appendLog("Error parsing video information: " + e.getMessage());
                enableDownloadButton();
                e.printStackTrace();
            }

        } catch (Exception e) {
            appendLog("Error: " + e.getMessage());
            enableDownloadButton();
            e.printStackTrace();
        }
    }

    private String getBestAdaptiveFormat(JsonObject streamingData) {
        String selectedQuality = (String) qualityCombo.getSelectedItem();
        try {
            var formats = streamingData.getAsJsonArray("adaptiveFormats");
            String bestUrl = null;
            int bestQuality = 0;

            for (var element : formats) {
                JsonObject format = element.getAsJsonObject();
                if (format.has("url") && format.has("qualityLabel")) {
                    String url = format.get("url").getAsString();
                    String qualityLabel = format.get("qualityLabel").getAsString();
                    int height = Integer.parseInt(qualityLabel.replaceAll("[^0-9]", ""));

                    if (selectedQuality.equals("Highest Quality") && height > bestQuality) {
                        bestQuality = height;
                        bestUrl = url;
                    } else if (selectedQuality.equals("Medium Quality") && height >= 480 && height <= 720) {
                        bestUrl = url;
                        break;
                    } else if (selectedQuality.equals("Low Quality") && height <= 480) {
                        bestUrl = url;
                        break;
                    }
                }
            }

            return bestUrl;
        } catch (Exception e) {
            appendLog("Error parsing adaptive formats: " + e.getMessage());
            return null;
        }
    }

    private String getBestStandardFormat(JsonObject streamingData) {
        try {
            var formats = streamingData.getAsJsonArray("formats");
            if (formats.size() > 0) {
                // Just get the first format as fallback
                return formats.get(0).getAsJsonObject().get("url").getAsString();
            }
        } catch (Exception e) {
            appendLog("Error parsing standard formats: " + e.getMessage());
        }
        return null;
    }

    private String extractVideoId(String url) {
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    private String fetchUrlContent(String url) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "Mozilla/5.0")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private Map<String, String> parseQueryString(String query) throws UnsupportedEncodingException {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return params;
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0) {
                String key = URLDecoder.decode(pair.substring(0, idx), "UTF-8");
                String value = "";
                if (idx + 1 < pair.length()) {
                    value = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
                }
                params.put(key, value);
            }
        }
        return params;
    }

    private void downloadFile(String fileUrl, String outputFolder, String title) {
        try {
            String fileName = sanitizeFilename(title) + ".mp4";
            Path outputPath = Paths.get(outputFolder, fileName);

            appendLog("Downloading to: " + outputPath);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(fileUrl))
                    .header("User-Agent", "Mozilla/5.0")
                    .build();

            HttpResponse<InputStream> response = client.send(request,
                    HttpResponse.BodyHandlers.ofInputStream());

            long fileSize = response.headers().firstValueAsLong("Content-Length").orElse(0);
            InputStream in = response.body();

            try (FileOutputStream out = new FileOutputStream(outputPath.toFile())) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                long totalRead = 0;
                long lastUpdateTime = 0;

                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                    totalRead += bytesRead;

                    // Update progress at most every 200ms to avoid UI lag
                    long currentTime = System.currentTimeMillis();
                    if (fileSize > 0 && (currentTime - lastUpdateTime > 200 || totalRead == fileSize)) {
                        int progress = (int) ((totalRead * 100) / fileSize);
                        SwingUtilities.invokeLater(() -> progressBar.setValue(progress));
                        lastUpdateTime = currentTime;
                    }
                }
            }

            appendLog("Download completed: " + fileName);
            SwingUtilities.invokeLater(() -> {
                progressBar.setValue(100);
                JOptionPane.showMessageDialog(this, "Download completed!", "Success", JOptionPane.INFORMATION_MESSAGE);
                enableDownloadButton();
            });

        } catch (Exception e) {
            appendLog("Download failed: " + e.getMessage());
            SwingUtilities.invokeLater(() -> {
                progressBar.setValue(0);
                enableDownloadButton();
            });
        }
    }

    private String sanitizeFilename(String filename) {
        return filename.replaceAll("[^a-zA-Z0-9.-]", "_");
    }

    private void appendLog(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private void enableDownloadButton() {
        SwingUtilities.invokeLater(() -> downloadButton.setEnabled(true));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            YouTubeDownloader downloader = new YouTubeDownloader();
            downloader.setVisible(true);
        });
    }
}