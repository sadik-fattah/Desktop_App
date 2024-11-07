package streaming;

import java.io.*;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.nio.charset.StandardCharsets;

public class MjpegWriter implements AutoCloseable {

    private static final byte[] CRLF = {13, 10};
    private static final byte[] EmptyLine = {13, 10, 13, 10};

    private String boundary;
    private OutputStream stream;

    public MjpegWriter(OutputStream stream) {
        this(stream, "--boundary");
    }

    public MjpegWriter(OutputStream stream, String boundary) {
        this.stream = stream;
        this.boundary = boundary;
    }

    public String getBoundary() {
        return boundary;
    }

    public OutputStream getStream() {
        return stream;
    }

    public void writeHeader() throws IOException {
        write(
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: multipart/x-mixed-replace; boundary=" +
                        this.boundary +
                        "\r\n"
        );

        this.stream.flush();
    }

    public void write(Image image) throws IOException {
        ByteArrayOutputStream baos = bytesOf(image);
        this.write(baos);
    }

    public void write(ByteArrayOutputStream imageStream) throws IOException {
        StringBuilder sb = new StringBuilder();

        sb.append("\r\n");
        sb.append(this.boundary).append("\r\n");
        sb.append("Content-Type: image/jpeg\r\n");
        sb.append("Content-Length: ").append(imageStream.size()).append("\r\n");
        sb.append("\r\n");

        write(sb.toString());
        imageStream.writeTo(this.stream);
        write("\r\n");

        this.stream.flush();
    }

    private void write(byte[] data) throws IOException {
        this.stream.write(data);
    }

    private void write(String text) throws IOException {
        byte[] data = bytesOf(text);
        this.stream.write(data);
    }

    private static byte[] bytesOf(String text) {
        return text.getBytes(StandardCharsets.US_ASCII);
    }

    private static ByteArrayOutputStream bytesOf(Image image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write((BufferedImage) image, "jpg", baos);
        return baos;
    }

    public String readRequest(int length) throws IOException {
        byte[] data = new byte[length];
        int count = this.stream.read(data, 0, data.length);

        if (count != 0) {
            return new String(data, 0, count, StandardCharsets.US_ASCII);
        }

        return null;
    }

    @Override
    public void close() throws IOException {
        if (this.stream != null) {
            this.stream.close();
        }
        this.stream = null;
    }
}

