import streaming.Img_Streaming_toServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;

public class VideoStreamer extends JFrame {

    private Img_Streaming_toServer server;
    private JLabel linkLabel;
    private JLabel statusLabel;

    public VideoStreamer() throws IOException {
        super("Video Streamer");

        // Create UI elements
        linkLabel = new JLabel("http://192.168.1.13:8088");
        linkLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        linkLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(linkLabel.getText()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        statusLabel = new JLabel("Clients: 0");

        // Create the layout
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(linkLabel, BorderLayout.CENTER);
        panel.add(statusLabel, BorderLayout.SOUTH);

        add(panel);

        // Start the server
        server = new Img_Streaming_toServer();
        server.start(8088);

        // Update client count periodically
        new Timer(1000, e -> {
            int count = server.getClients().size();
            statusLabel.setText("Clients: " + count);
        }).start();

        // Set window properties
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 100);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) throws IOException {
        new VideoStreamer();
    }
}