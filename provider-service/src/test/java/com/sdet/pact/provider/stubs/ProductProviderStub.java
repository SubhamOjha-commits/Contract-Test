package com.sdet.pact.provider.stubs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stub implementation of Product Provider Service
 */
public class ProductProviderStub {

    private HttpServer server;
    private final int port;
    private final Map<Integer, Product> products = new HashMap<>();
    private final Map<Integer, ProductDetails> productDetails = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProductProviderStub(int port) {
        this.port = port;
    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            
            server.createContext("/api/products", this::handleProducts);
            
            server.setExecutor(null);
            server.start();
            System.out.println("Product Provider Stub started on port " + port);
        } catch (IOException e) {
            throw new RuntimeException("Failed to start stub server", e);
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("Product Provider Stub stopped");
        }
    }

    private void handleProducts(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery();
        
        if ("GET".equals(method)) {
            if (path.equals("/api/products")) {
                handleGetAllProducts(exchange);
            } else {
                handleGetProductById(exchange, path, query);
            }
        } else {
            sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
        }
    }

    private void handleGetAllProducts(HttpExchange exchange) throws IOException {
        List<Product> productList = new ArrayList<>(products.values());
        String response = objectMapper.writeValueAsString(productList);
        sendResponse(exchange, 200, response);
    }

    private void handleGetProductById(HttpExchange exchange, String path, String query) throws IOException {
        try {
            String[] parts = path.split("/");
            int productId = Integer.parseInt(parts[parts.length - 1]);
            
            Product product = products.get(productId);
            if (product == null) {
                sendResponse(exchange, 404, "{\"error\": \"Product not found\"}");
                return;
            }

            boolean includeDetails = query != null && query.contains("includeDetails=true");
            
            if (includeDetails) {
                ProductWithDetails productWithDetails = new ProductWithDetails(product);
                ProductDetails details = productDetails.get(productId);
                if (details != null) {
                    productWithDetails.details = details;
                }
                String response = objectMapper.writeValueAsString(productWithDetails);
                sendResponse(exchange, 200, response);
            } else {
                String response = objectMapper.writeValueAsString(product);
                sendResponse(exchange, 200, response);
            }
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "{\"error\": \"Invalid product ID\"}");
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

    public void addProduct(int id, String name, double price, String category, boolean inStock) {
        products.put(id, new Product(id, name, price, category, inStock));
    }

    public void addProductDetails(int productId, String manufacturer, String warranty, int stock) {
        productDetails.put(productId, new ProductDetails(manufacturer, warranty, stock));
    }

    public void removeProduct(int id) {
        products.remove(id);
        productDetails.remove(id);
    }

    // Inner classes

    public static class Product {
        public int id;
        public String name;
        public double price;
        public String category;
        public boolean inStock;

        public Product() {}

        public Product(int id, String name, double price, String category, boolean inStock) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.category = category;
            this.inStock = inStock;
        }
    }

    public static class ProductDetails {
        public String manufacturer;
        public String warranty;
        public int stock;

        public ProductDetails() {}

        public ProductDetails(String manufacturer, String warranty, int stock) {
            this.manufacturer = manufacturer;
            this.warranty = warranty;
            this.stock = stock;
        }
    }

    public static class ProductWithDetails extends Product {
        public ProductDetails details;

        public ProductWithDetails() {}

        public ProductWithDetails(Product product) {
            super(product.id, product.name, product.price, product.category, product.inStock);
        }
    }
}

