package org.guercifzone;

import org.guercifzone.Utils.FileUtils;
import org.guercifzone.Utils.YoutubeUtils;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Downloader extends SwingWorker<Void, String> {
    private String playlistUrl;
    private String savePath;
    private String format;
    private JPanel progressPanelContainer;

    public Downloader(String playlistUrl, String savePath, String format, JPanel progressPanelContainer) {
        this.playlistUrl = playlistUrl;
        this.savePath = savePath;
        this.format = format;
        this.progressPanelContainer = progressPanelContainer;
    }

    @Override
    protected Void doInBackground() throws Exception {
        List<String> videoUrls = YoutubeUtils.extractVideoUrlsFromPlaylist(playlistUrl);

        for (String videoUrl : videoUrls) {
            String videoTitle = YoutubeUtils.getVideoTitle(videoUrl);
            publish(videoTitle);

            ProgressPanel progressPanel = new ProgressPanel(videoTitle);
            SwingUtilities.invokeLater(() -> {
                progressPanelContainer.add(progressPanel);
                progressPanelContainer.revalidate();
                progressPanelContainer.repaint();
            });

            // Simulate download (in a real app, you'd use youtube-dl or similar)
            for (int i = 0; i <= 100; i++) {
                Thread.sleep(50);
                progressPanel.updateProgress(i);
            }

            // Save file
            FileUtils.saveVideo(videoUrl, savePath, format, videoTitle);
        }

        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        for (String title : chunks) {
            System.out.println("Downloading: " + title);
        }
    }

    @Override
    protected void done() {
        try {
            get();
            JOptionPane.showMessageDialog(null,
                    "Download completed successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (InterruptedException | ExecutionException e) {
            JOptionPane.showMessageDialog(null,
                    "Error during download: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}