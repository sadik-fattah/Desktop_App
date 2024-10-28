package org.guercifzone.streamingimage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.OutputStream;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

public class ScreenStreamer {
    private static final int PORT = 8080;

    public static void main(String[] args) throws Exception {
        // Start the HTTP server
        HttpServer server = HttpServer.create(new java.net.InetSocketAddress(PORT), 0);
        server.createContext("/stream", new StreamHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port " + PORT);

        // Start the JFrame
        JFrame frame = new JFrame("Screen Streamer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setVisible(true);


        //timer
        Timer timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new StreamHandler();
            }
        });
        timer.start();
    }

    static class StreamHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Capture the screen
            BufferedImage screenImage = captureScreen();
            exchange.getResponseHeaders().set("Content-Type", "image/png");
            exchange.sendResponseHeaders(200, 0);
            OutputStream os = exchange.getResponseBody();
            ImageIO.write(screenImage, "png", os);
            os.close();
        }

        private BufferedImage captureScreen() {
            try {
                Robot robot = new Robot();
                Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
                return robot.createScreenCapture(screenRect);
            } catch (AWTException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
