package org.guercifzone.streamingvideo;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ScreenStreamer {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("Server started on port 8080");

            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    OutputStream os = clientSocket.getOutputStream();
                    os.write("HTTP/1.0 200 OK\r\n".getBytes());
                    os.write("Content-Type: multipart/x-mixed-replace; boundary=--myboundary\r\n\r\n".getBytes());

                    while (true) {
                        BufferedImage image = captureScreen();
                        if (image != null) {
                            os.write("--myboundary\r\n".getBytes());
                            os.write("Content-Type: image/jpeg\r\n".getBytes());
                            os.write("Content-Length: ".getBytes());
                            os.write(String.valueOf(image.getWidth() * image.getHeight()).getBytes());
                            os.write("\r\n\r\n".getBytes());
                            ImageIO.write(image, "jpeg", os);
                            os.write("\r\n\r\n".getBytes());
                            os.flush();
                        }
                        Thread.sleep(100); // Adjust for frame rate
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage captureScreen() {
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
