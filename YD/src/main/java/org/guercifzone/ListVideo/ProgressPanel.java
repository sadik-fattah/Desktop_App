package org.guercifzone.ListVideo;


import javax.swing.*;
import java.awt.*;

public class ProgressPanel extends JPanel {
    private JLabel titleLabel;
    private JProgressBar progressBar;

    public ProgressPanel(String title) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        titleLabel = new JLabel(title);
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        add(titleLabel, BorderLayout.NORTH);
        add(progressBar, BorderLayout.CENTER);
    }

    public void updateProgress(int progress) {
        progressBar.setValue(progress);
    }
}