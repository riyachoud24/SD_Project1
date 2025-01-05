import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClothingShopServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/products", new ProductHandler());
        server.setExecutor(null);
        System.out.println("My Server is running on http://localhost:8080");
        server.start();
    }
}

class Product {
    private String name;
    private double price;
    private int stock;

    public Product(String name, double price, int stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }

    public void setPrice(double price) { this.price = price; }
    public void setStock(int stock) { this.stock = stock; }

    public String toJSON() {
        return String.format("{\"name\":\"%s\",\"price\":%.2f,\"stock\":%d}", name, price, stock);
    }

    public static Product fromJSON(String json) {
        try {
            String[] parts = json.replace("{", "").replace("}", "").replace("\"", "").split(",");
            String name = parts[0].split(":")[1];
            double price = Double.parseDouble(parts[1].split(":")[1]);
            int stock = Integer.parseInt(parts[2].split(":")[1]);
            return new Product(name, price, stock);
        } catch (Exception e) {
            return null;
        }
    }
}

class ProductHandler implements HttpHandler {
    private final String DB_URL = "jdbc:postgresql://localhost:5432/clothing_shop";
    private final String DB_USER = "riyachoudhary";
    private final String DB_PASSWORD = ""; 

    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String method = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery();
        String response = "";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            switch (method) {
                case "GET":
                    if (query != null && query.startsWith("name=")) {
                        String name = query.substring(5).toLowerCase();
                        response = getProductByName(connection, name);
                    } else {
                        response = getAllProducts(connection);
                    }
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                    break;

                case "POST":
                    Product newProduct = Product.fromJSON(readRequestBody(exchange.getRequestBody()));
                    if (newProduct != null) {
                        addProduct(connection, newProduct);
                        response = "{\"message\":\"Product added successfully\"}";
                        exchange.sendResponseHeaders(201, response.getBytes().length);
                    } else {
                        response = "{\"error\":\"Invalid data\"}";
                        exchange.sendResponseHeaders(400, response.getBytes().length);
                    }
                    break;

                case "PUT":
                    Product updatedProduct = Product.fromJSON(readRequestBody(exchange.getRequestBody()));
                    if (updatedProduct != null) {
                        System.out.println(updatedProduct.getName());
                        updateProduct(connection, updatedProduct);
                        response = "{\"message\":\"Product updated successfully\"}";
                        exchange.sendResponseHeaders(200, response.getBytes().length);
                    } else {
                        response = "{\"error\":\"Invalid data\"}";
                        exchange.sendResponseHeaders(400, response.getBytes().length);
                    }
                    break;

                case "DELETE":
                    if (query != null && query.startsWith("name=")) {
                        String nameToDelete = query.substring(5).toLowerCase();
                        deleteProduct(connection, nameToDelete);
                        response = "{\"message\":\"Product deleted successfully\"}";
                        exchange.sendResponseHeaders(200, response.getBytes().length);
                    } else {
                        response = "{\"error\":\"Invalid request\"}";
                        exchange.sendResponseHeaders(400, response.getBytes().length);
                    }
                    break;

                default:
                    response = "{\"error\":\"Method not supported\"}";
                    exchange.sendResponseHeaders(405, response.getBytes().length);
            }
        
        connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
            response = "{\"error\":\"Database error\"}";
            exchange.sendResponseHeaders(500, response.getBytes().length);
        }
        
        OutputStream os = exchange.getResponseBody();
        System.out.println(response);
        os.write(response.getBytes());
        os.close();
    }

    private String getProductByName(Connection connection, String name) throws SQLException {
        String sql = "SELECT * FROM product WHERE LOWER(name) = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    name = rs.getString("name");
                    double price = rs.getDouble("price");
                    int stock = rs.getInt("stock");
                    return new Product(name, price, stock).toJSON();
                } else {
                    return "{\"error\":\"Product not found\"}";
                }
            }
        }
    }

    private String getAllProducts(Connection connection) throws SQLException {
        String sql = "SELECT * FROM product";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            List<String> products = new ArrayList<>();
            while (rs.next()) {
                products.add(new Product(rs.getString("name"), rs.getDouble("price"), rs.getInt("stock")).toJSON());
            }
            return "[" + String.join(",", products) + "]";
        }
    }

    private void addProduct(Connection connection, Product product) throws SQLException {
        String sql = "INSERT INTO product (name, price, stock) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getPrice());
            stmt.setInt(3, product.getStock());
            stmt.executeUpdate();
        }
    }

    private void updateProduct(Connection connection, Product product) throws SQLException {
        String sql = "UPDATE product SET price = ?, stock = ? WHERE LOWER(name) = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, product.getPrice());
            stmt.setInt(2, product.getStock());
            stmt.setString(3, product.getName().toLowerCase());
            stmt.executeUpdate();
        }
    }

    private void deleteProduct(Connection connection, String name) throws SQLException {
        String sql = "DELETE FROM product WHERE LOWER(name) = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        }
    }

    private String readRequestBody(InputStream is) throws IOException {
        return new String(is.readAllBytes());
    }
}

