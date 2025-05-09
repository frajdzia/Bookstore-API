# 📚 Bookstore API

## Overview

This API allows clients to interact with the following entities:

### 📖 Authors
Creators of books, with attributes such as:
- `name`
- `biography`

### 📘 Books
Products with details like:
- `title`
- `author`
- `ISBN`
- `publication year`
- `price`
- `stock quantity`

### 👤 Customers
Users of the bookstore, identified by:
- `name`
- `email`
- `password` (simple)

### 🛒 Carts
Shopping carts linked to customers. Supports:
- Adding books
- Removing books
- Updating quantity of books

### 🧾 Orders
Represent completed purchases, generated from a customer's cart.


## 🛠️ Technologies Used:
Java, Maven (WAR packaging), JAX-RS (Jakarta REST) with Jersey, Jackson (JSON), Jetty (server), SLF4J (logging), and Postman (testing).


## 🚀 How to Run

1. Make sure Java and Maven are installed.
2. Clone the repository.
3. Run the API using Jetty:

```bash
mvn jetty:run
```

## 🧪 Testing
Test cases will be organized by model (Author, Book, Customer, Cart, Order) and cover:

`GET`, `POST`, `PUT`, `DELETE` methods

