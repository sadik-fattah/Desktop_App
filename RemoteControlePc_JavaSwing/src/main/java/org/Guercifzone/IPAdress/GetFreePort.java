package org.Guercifzone.IPAdress;


import java.net.ServerSocket;

public class GetFreePort {
    private boolean isPortAvailable(int port) {
        boolean portAvailable = true;
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port);
        } catch (Exception var13) {
            portAvailable = false;
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        return portAvailable;
    }

    public int getFreePort() {
        int port;
        for(port = 3000; !this.isPortAvailable(port); ++port) {
        }

        return port;
    }
}