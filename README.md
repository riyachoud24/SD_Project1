# Clothing Shop API and Frontend

This project is a simple clothing shop management system consisting of a backend API and a frontend interface. It allows users to manage products in a store, including searching, adding, updating, and deleting products. The backend is implemented in Java, while the frontend uses HTML, CSS, and JavaScript.

## Features

- **Search Products**: Retrieve details of a product by its name or fetch all products.
- **Add Product**: Add new products to the inventory.
- **Update Product**: Update price and stock for an existing product.
- **Delete Product**: Remove a product from the inventory.

## Tech Stack

### Backend
- **Language**: Java
- **Framework**: [HttpServer](https://docs.oracle.com/en/java/javase/11/docs/api/java.net.http/java/net/http/HttpServer.html)
- **Database**: PostgreSQL

### Frontend
- **HTML**: Structure of the web interface
- **CSS**: Styling for a user-friendly interface
- **JavaScript**: Client-side logic to interact with the backend API

## Setup Instructions

### Prerequisites
- Java Development Kit (JDK) 11 or higher
- PostgreSQL database
- Node.js (optional for additional testing tools)
- A modern web browser

### Database Setup
1. Create a PostgreSQL database named `clothing_shop`.
2. Create a table using the following SQL:
   ```sql
   CREATE TABLE product (
       name VARCHAR(255) PRIMARY KEY,
       price DECIMAL(10, 2) NOT NULL,
       stock INT NOT NULL
   );
   ```
3. Update the database connection credentials in the `ProductHandler` class:
   ```java
   private final String DB_URL = "jdbc:postgresql://localhost:5432/clothing_shop";
   private final String DB_USER = "your_username";
   private final String DB_PASSWORD = "your_password";
   ```

### Running the Backend
1. Compile and run the `ClothingShopServer.java` file:
   ```bash
   javac ClothingShopServer.java
   java ClothingShopServer
   ```
2. The server will start and listen on `http://localhost:8080`.

### Running the Frontend
1. Open the `index.html` file in a web browser.
2. Use the interface to interact with the backend.

## API Endpoints

### Base URL
`http://localhost:8080/products`

### Endpoints

#### GET `/products`
- **Description**: Fetch all products.
- **Response**:
  ```json
  [
      {"name":"Shirt","price":19.99,"stock":50},
      {"name":"Jeans","price":39.99,"stock":20}
  ]
  ```

#### GET `/products?name={productName}`
- **Description**: Fetch details of a specific product.
- **Response**:
  ```json
  {"name":"Shirt","price":19.99,"stock":50}
  ```

#### POST `/products`
- **Description**: Add a new product.
- **Request Body**:
  ```json
  {"name":"Hat","price":14.99,"stock":10}
  ```
- **Response**:
  ```json
  {"message":"Product added successfully"}
  ```

#### PUT `/products`
- **Description**: Update an existing product.
- **Request Body**:
  ```json
  {"name":"Shirt","price":24.99,"stock":40}
  ```
- **Response**:
  ```json
  {"message":"Product updated successfully"}
  ```

#### DELETE `/products?name={productName}`
- **Description**: Delete a product by name.
- **Response**:
  ```json
  {"message":"Product deleted successfully"}
  ```

## Frontend Interface
- **Search Section**: Search for a product by its name.
- **Add Product Section**: Input fields to add a new product.
- **Update Product Section**: Update price and stock for an existing product.
- **Delete Product Section**: Delete a product by its name.

## Future Improvements
- Implement authentication for API endpoints.
- Add product categories and descriptions.
- Implement pagination for large datasets.
- Create a responsive UI for mobile devices.
