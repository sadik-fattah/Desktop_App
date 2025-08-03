package org.guercifzone;



import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AppFrame extends JFrame {
    private JTextField urlField;
    private JButton downloadButton;
    private JComboBox<String> formatComboBox;
    private JFileChooser directoryChooser;
    private JPanel progressPanelContainer;

    public AppFrame() {
        setTitle("YouTube Playlist Downloader");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        layoutComponents();

        setVisible(true);
    }

    private void initComponents() {
        urlField = new JTextField(30);
        downloadButton = new JButton("Download");
        formatComboBox = new JComboBox<>(new String[]{"MP3", "MP4"});
        directoryChooser = new JFileChooser();
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        progressPanelContainer = new JPanel();
        progressPanelContainer.setLayout(new BoxLayout(progressPanelContainer, BoxLayout.Y_AXIS));

        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String playlistUrl = urlField.getText();
                String format = (String) formatComboBox.getSelectedItem();

                if (playlistUrl.isEmpty()) {
                    JOptionPane.showMessageDialog(AppFrame.this,
                            "Please enter a YouTube playlist URL",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int returnVal = directoryChooser.showSaveDialog(AppFrame.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    String savePath = directoryChooser.getSelectedFile().getAbsolutePath();
                    new Downloader(playlistUrl, savePath, format.toLowerCase(), progressPanelContainer).execute();
                }
            }
        });
    }

    private void layoutComponents() {
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Playlist URL:"));
        inputPanel.add(urlField);
        inputPanel.add(new JLabel("Format:"));
        inputPanel.add(formatComboBox);
        inputPanel.add(downloadButton);

        JScrollPane scrollPane = new JScrollPane(progressPanelContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
}