package org.guercifzone.Classes;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.guercifzone.DataBase.DatabaseHelper;


import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class LoginHandler implements HttpHandler {
    private DatabaseHelper dbHelper = new DatabaseHelper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            handlePostRequest(exchange);
        } else {
            sendResponse(exchange, 405, "Method Not Allowed");
        }
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        try {
            // Parse form data
            URI requestUri = exchange.getRequestURI();
            String query = requestUri.getQuery();
            Map<String, String> params = parseQuery(query);

            String email = params.get("email");
            String password = params.get("password");
            String ipAddress = exchange.getRemoteAddress().getAddress().getHostAddress();

            if (email == null || password == null) {
                sendResponse(exchange, 400, "Bad Request: Missing email or password");
                return;
            }

            if (dbHelper.validateUser(email, password)) {
                dbHelper.updateLoginInfo(email, ipAddress);
                sendResponse(exchange, 200, "Login successful from IP: " + ipAddress);
            } else {
                // If user doesn't exist, create new account
                if (dbHelper.addUser(email, password, ipAddress)) {
                    sendResponse(exchange, 201, "Account created and login successful from IP: " + ipAddress);
                } else {
                    sendResponse(exchange, 401, "Invalid credentials");
                }
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, "Internal Server Error: " + e.getMessage());
        }
    }

    private Map<String, String> parseQuery(String query) {
        Map<String, String> result = new HashMap<>();
        if (query == null) return result;

        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length > 1) {
                result.put(pair[0], pair[1]);
            }
        }
        return result;
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
