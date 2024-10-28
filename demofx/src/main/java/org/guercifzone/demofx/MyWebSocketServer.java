package org.guercifzone.demofx;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class MyWebSocketServer extends WebSocketServer {
    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {

    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {

    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {

    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
e.printStackTrace();
    }

    @Override
    public void onStart() {
 System.out.println("Server started! on port: " + getPort());
    }
    public static void main(String[] args) {
        MyWebSocketServer server = new MyWebSocketServer();
        server.start();
    }

}





