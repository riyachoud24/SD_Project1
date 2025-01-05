// Helper function to display messages
function displayMessage(message) {
    const messageContainer = document.getElementById("messageContainer");
    messageContainer.innerHTML = message;
}

// Search Product
document.getElementById("searchButton").addEventListener("click", () => {
    console.log("Search button clicked");

    const productName = document.getElementById("searchInput").value;
    console.log("Product name entered:", productName);

    fetch(`http://localhost:8080/products?name=${productName}`)
        .then(response => {
            console.log("Received response from server. Status:", response.status);
            if (response.ok) {
                return response.json();
            } else {
                console.log("Product not found. Throwing error...");
                throw new Error("Product not found");
            }
        })
        .then(data => {
            console.log("Parsed product data:", data);
            if (Array.isArray(data)) {
                if (data.length > 0) {
                    data.forEach(product => {
                        displayMessage(`Product: ${product.name}, Price: $${product.price}`);
                    });
                } else {
                    displayMessage("No products found.");
                }
            } else {
                displayMessage(`Product: ${data.name}, Price: $${data.price}`);
            }
        })
        .catch(error => {
            console.error("Error during fetch:", error);
            displayMessage("An error occurred while fetching the product.");
        });
});

// Add Product
document.getElementById("addButton").addEventListener("click", () => {
    console.log("Add button clicked");

    const name = document.getElementById("addName").value;
    const price = parseFloat(document.getElementById("addPrice").value);
    const stock = parseInt(document.getElementById("addStock").value);

    console.log("Product details entered:", { name, price, stock });

    fetch("http://localhost:8080/products", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name, price, stock }),
    })
        .then(response => {
            console.log("Received response from server. Status:", response.status);
            if (response.ok) {
                console.log("Product added successfully");
                displayMessage("Product added successfully");
            } else {
                console.log("Failed to add product. Throwing error...");
                throw new Error("Failed to add product");
            }
        })
        .catch(error => {
            console.error("Error during fetch:", error.message);
            displayMessage(error.message);
        });
});

// Update Product
document.getElementById("updateButton").addEventListener("click", () => {
    console.log("Update button clicked");

    const name = document.getElementById("updateName").value;
    const price = parseFloat(document.getElementById("updatePrice").value);
    const stock = parseInt(document.getElementById("updateStock").value);

    console.log("Updated product details:", { name, price, stock });

    fetch("http://localhost:8080/products", {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name, price, stock }),
    })
        .then(response => {
            console.log("Received response from server. Status:", response.status);
            if (response.ok) {
                console.log("Product updated successfully");
                displayMessage("Product updated successfully");
            } else {
                console.log("Failed to update product. Throwing error...");
                throw new Error("Failed to update product");
            }
        })
        .catch(error => {
            console.error("Error during fetch:", error.message);
            displayMessage(error.message);
        });
});

// Delete Product
document.getElementById("deleteButton").addEventListener("click", () => {
    console.log("Delete button clicked");

    const productName = document.getElementById("deleteName").value;
    console.log("Product name to delete:", productName);

    fetch(`http://localhost:8080/products?name=${productName}`, {
        method: "DELETE",
    })
        .then(response => {
            console.log("Received response from server. Status:", response.status);
            if (response.ok) {
                console.log("Product deleted successfully");
                displayMessage("Product deleted successfully");
            } else {
                console.log("Failed to delete product. Throwing error...");
                throw new Error("Failed to delete product");
            }
        })
        .catch(error => {
            console.error("Error during fetch:", error.message);
            displayMessage(error.message);
        });
});
