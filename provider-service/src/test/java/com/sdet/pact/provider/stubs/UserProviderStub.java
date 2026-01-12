package com.sdet.pact.provider.stubs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Stub implementation of User Provider Service
 * 
 * This is a lightweight HTTP server that simulates the provider service
 * for verification testing purposes.
 */
public class UserProviderStub {

    private HttpServer server;
    private final int port;
    private final Map<Integer, User> users = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private boolean userCreationEnabled = false;

    public UserProviderStub(int port) {
        this.port = port;
    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);

            // Handle all /api/users requests
            server.createContext("/api/users", this::handleUserRequests);
            
            server.setExecutor(null);
            server.start();
            System.out.println("User Provider Stub started on port " + port);
        } catch (IOException e) {
            throw new RuntimeException("Failed to start stub server", e);
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("User Provider Stub stopped");
        }
    }

    private void handleUserRequests(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("POST".equals(method) && path.equals("/api/users")) {
            handleCreateUser(exchange);
        } else if ("GET".equals(method) && path.startsWith("/api/users/")) {
            handleGetUserById(exchange, path);
        } else if ("PUT".equals(method) && path.startsWith("/api/users/")) {
            handleUpdateUser(exchange, path);
        } else if ("DELETE".equals(method) && path.startsWith("/api/users/")) {
            handleDeleteUser(exchange, path);
        } else {
            sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
        }
    }

    private void handleGetUserById(HttpExchange exchange, String path) throws IOException {
        try {
            String[] parts = path.split("/");
            int userId = Integer.parseInt(parts[parts.length - 1]);
            
            User user = users.get(userId);
            if (user != null) {
                String response = objectMapper.writeValueAsString(user);
                sendResponse(exchange, 200, response);
            } else {
                String errorResponse = String.format(
                    "{\"error\": \"User not found\", \"status\": 404}");
                sendResponse(exchange, 404, errorResponse);
            }
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "{\"error\": \"Invalid user ID\"}");
        }
    }

    private void handleCreateUser(HttpExchange exchange) throws IOException {
        if (!userCreationEnabled) {
            sendResponse(exchange, 403, "{\"error\": \"User creation not allowed\"}");
            return;
        }

        String requestBody = new String(exchange.getRequestBody().readAllBytes());
        Map<String, Object> userData = objectMapper.readValue(requestBody, Map.class);
        
        int newId = users.size() + 1;
        User newUser = new User(
            newId,
            (String) userData.get("name"),
            (String) userData.get("email"),
            (String) userData.get("role"),
            true
        );
        
        users.put(newId, newUser);
        String response = objectMapper.writeValueAsString(newUser);
        sendResponse(exchange, 201, response);
    }

    private void handleUpdateUser(HttpExchange exchange, String path) throws IOException {
        try {
            String[] parts = path.split("/");
            int userId = Integer.parseInt(parts[parts.length - 1]);
            
            if (!users.containsKey(userId)) {
                sendResponse(exchange, 404, "{\"error\": \"User not found\"}");
                return;
            }

            String requestBody = new String(exchange.getRequestBody().readAllBytes());
            Map<String, Object> updateData = objectMapper.readValue(requestBody, Map.class);
            
            User existingUser = users.get(userId);
            User updatedUser = new User(
                userId,
                (String) updateData.getOrDefault("name", existingUser.name),
                (String) updateData.getOrDefault("email", existingUser.email),
                existingUser.role,
                existingUser.active
            );
            
            users.put(userId, updatedUser);
            String response = objectMapper.writeValueAsString(updatedUser);
            sendResponse(exchange, 200, response);
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "{\"error\": \"Invalid user ID\"}");
        }
    }

    private void handleDeleteUser(HttpExchange exchange, String path) throws IOException {
        try {
            String[] parts = path.split("/");
            int userId = Integer.parseInt(parts[parts.length - 1]);
            
            if (users.remove(userId) != null) {
                sendResponse(exchange, 204, "");
            } else {
                sendResponse(exchange, 404, "{\"error\": \"User not found\"}");
            }
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "{\"error\": \"Invalid user ID\"}");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        byte[] bytes = response.getBytes();
        exchange.sendResponseHeaders(statusCode, bytes.length);

        if (bytes.length > 0) {
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        } else {
            exchange.getResponseBody().close();
        }
    }

    // Public methods for test setup

    public void addUser(int id, String name, String email, String role, boolean active) {
        users.put(id, new User(id, name, email, role, active));
    }

    public void removeUser(int id) {
        users.remove(id);
    }

    public void enableUserCreation() {
        this.userCreationEnabled = true;
    }

    public void disableUserCreation() {
        this.userCreationEnabled = false;
    }

    // Inner class representing User entity
    public static class User {
        public int id;
        public String name;
        public String email;
        public String role;
        public boolean active;

        public User() {}

        public User(int id, String name, String email, String role, boolean active) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.role = role;
            this.active = active;
        }
    }
}
