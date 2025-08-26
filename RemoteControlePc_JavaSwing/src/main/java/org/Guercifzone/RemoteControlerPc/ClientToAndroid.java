package org.Guercifzone.RemoteControlerPc;


import org.Guercifzone.Classes.AvatarFile;


import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import javax.swing.SwingWorker;

public class ClientToAndroid {
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static InputStream inputStream;
    private static OutputStream outputStream;
    public static ObjectInputStream objectInputStream;
    private static ObjectOutputStream objectOutputStream;

    public void connect(final InetAddress inetAddress, final int port) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                Thread.sleep(3000L);
                connectToAndroid(inetAddress, port);
                return null;
            }

            @Override
            protected void done() {
                // Optional: handle completion
            }
        }.execute();
    }

    private void connectToAndroid(InetAddress inetAddress, int port) {
        try {
            SocketAddress socketAddress = new InetSocketAddress(inetAddress, port);
            clientSocket = new Socket();
            clientSocket.connect(socketAddress, 3000);
            inputStream = clientSocket.getInputStream();
            outputStream = clientSocket.getOutputStream();
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectInputStream = new ObjectInputStream(inputStream);
            fetchDirectory("/");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeConnectionToAndroid() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }

            if (clientSocket != null) {
                clientSocket.close();
            }

            if (inputStream != null) {
                inputStream.close();
            }

            if (outputStream != null) {
                outputStream.close();
            }

            if (objectOutputStream != null) {
                objectOutputStream.close();
            }

            if (objectInputStream != null) {
                objectInputStream.close();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void sendMessageToAndroid(String message) {
        if (clientSocket != null) {
            try {
                objectOutputStream.writeObject(message);
                objectOutputStream.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void fetchDirectory(String path) {
        try {
            sendMessageToAndroid("FILE_DOWNLOAD_LIST_FILES");
            sendMessageToAndroid(path);
            ArrayList<AvatarFile> filesInFolder = (ArrayList<AvatarFile>) objectInputStream.readObject();
            if (filesInFolder != null && !filesInFolder.isEmpty()) {
                // Use SwingUtilities to update the UI thread safely
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        MainScreenController.mainScreenController.showFiles(filesInFolder);
                        MainScreenController.mainScreenController.displayPath(path);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}