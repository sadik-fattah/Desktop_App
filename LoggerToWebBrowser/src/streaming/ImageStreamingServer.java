package streaming;

import streaming.MjpegWriter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageStreamingServer implements AutoCloseable {

    private List<Socket> clients;
    private Thread thread;
    private Iterable<BufferedImage> imagesSource;
    private int interval;

    public ImageStreamingServer() {
        this(Screen.snapshots(600, 450, true));
    }

    public ImageStreamingServer(Iterable<BufferedImage> imagesSource) {
        this.clients = Collections.synchronizedList(new ArrayList<>());
        this.thread = null;
        this.imagesSource = imagesSource;
        this.interval = 50;
    }

    public Iterable<BufferedImage> getImagesSource() {
        return imagesSource;
    }

    public void setImagesSource(Iterable<BufferedImage> imagesSource) {
        this.imagesSource = imagesSource;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public Iterable<Socket> getClients() {
        return clients;
    }

    public boolean isRunning() {
        return (thread != null && thread.isAlive());
    }

    public void start(int port) {
        synchronized (this) {
            thread = new Thread(() -> serverThread(port));
            thread.setDaemon(true);
            thread.start();
        }
    }

    public void start() {
        this.start(8080);
    }

    public void stop() {
        if (this.isRunning()) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                synchronized (clients) {
                    for (Socket s : clients) {
                        try {
                            s.close();
                        } catch (IOException ignored) {
                        }
                    }
                    clients.clear();
                }
                thread = null;
            }
        }
    }

    private void serverThread(int port) {
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println(String.format("Server started on port %d.", port));
            while (true) {
                Socket client = server.accept();
                ExecutorService executor = Executors.newCachedThreadPool();
                executor.execute(() -> clientThread(client));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.stop();
        }
    }

    private void clientThread(Socket client) {
        System.out.println(String.format("New client from %s", client.getRemoteSocketAddress().toString()));
        synchronized (clients) {
            clients.add(client);
        }

        try (var outputStream = client.getOutputStream()) {
            MjpegWriter wr = new MjpegWriter(outputStream);
            wr.writeHeader();

            for (BufferedImage imgStream : Screen.streams(this.imagesSource)) {
                if (this.interval > 0) {
                    Thread.sleep(this.interval);
                }
                wr.write(imgStream);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            synchronized (clients) {
                clients.remove(client);
            }
        }
    }

    @Override
    public void close() {
        this.stop();
    }

    static class Screen {

        public static Iterable<BufferedImage> snapshots() {
            return snapshots(Toolkit.getDefaultToolkit().getScreenSize().width,
                    Toolkit.getDefaultToolkit().getScreenSize().height, true);
        }

        public static Iterable<BufferedImage> snapshots(int width, int height, boolean showCursor) {
            Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
            BufferedImage srcImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
            Graphics srcGraphics = srcImage.getGraphics();

            boolean scaled = (width != size.width || height != size.height);
            BufferedImage dstImage = srcImage;
            Graphics dstGraphics = srcGraphics;

            if (scaled) {
                dstImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                dstGraphics = dstImage.getGraphics();
            }

            Rectangle src = new Rectangle(0, 0, size.width, size.height);
            Rectangle dst = new Rectangle(0, 0, width, height);
            Dimension curSize = new Dimension(32, 32);

            while (true) {
                srcGraphics.copyArea(0, 0, size.width, size.height, 0, 0);
                if (showCursor) {
                    // Draw cursor if needed
                }
                if (scaled) {
                    dstGraphics.drawImage(srcImage, dst.x, dst.y, dst.width, dst.height, null);
                }
                yield return dstImage;
            }
        }

        public static Iterable<ByteArrayOutputStream> streams(Iterable<BufferedImage> source) {
            for (BufferedImage img : source) {
                ByteArrayOutputStream ms = new ByteArrayOutputStream();
                try {
                    ImageIO.write(img, "jpeg", ms);
                    Object yield;
                    yield return ms;
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        ms.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

