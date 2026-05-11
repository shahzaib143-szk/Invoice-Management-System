# 🧾 IMS — Invoice Management System

> A desktop-based business management application built with **Java**, **JavaFX**, **JDBC**, and **MySQL** — designed using Object-Oriented Programming principles and a clean layered software architecture.

---

## 📌 Overview

**IMS** simulates a real-world business management environment for small businesses.

Built as an academic OOP semester project, it demonstrates:

- Object-oriented software design
- Layered architecture (Model → DAO → Service → GUI)
- MySQL database integration via JDBC
- JavaFX desktop GUI with FXML
- Role-based authentication system
- Modular and maintainable backend engineering


## 🎥 Demo Video

[![Watch Demo](https://img.shields.io/badge/Watch-Demo-red?style=for-the-badge&logo=youtube)]
https://drive.google.com/file/d/1qwaaEypGefgJseSC60Ixnc4hG6iTQTlK/view?usp=drivesdk
---

## 🎯 Problem Statement

Small businesses often manage invoices and inventory manually or through poorly structured software — causing:

- Duplicate records
- Stock inconsistencies
- Difficult maintenance
- Tightly coupled, unmaintainable code

**IMS solves this** by providing a structured, layered desktop application with:

- Centralized customer management
- Real-time inventory tracking
- Dynamic invoice generation
- Role-based authentication
- Persistent MySQL database storage

---

## ✨ Features

### 🔐 Authentication & Access Control
- Secure login screen
- Admin and User role separation
- Session-based access control
- Dashboard routing based on role

### 👨‍💼 Admin Capabilities
- Full customer CRUD (Create, Read, Update, Delete)
- Full product management and stock control
- Invoice creation with multi-product cart
- View and manage all invoices
- Update invoice status
- Access complete system dashboard

### 👤 User Capabilities
- Personal login account
- View own invoices only
- View product listings
- Restricted system access

### 📦 Product & Inventory Management
- Add and update products
- Manage stock quantities
- Search products by name
- Low stock detection
- Automatic stock reduction on invoice creation

### 🧾 Invoice Management
- Dynamic multi-product invoice creation
- Shopping cart before finalization
- Tax and discount calculation
- Automatic total computation
- Invoice item persistence
- Customer-linked invoice records

---

## 🧠 System Architecture

```
JavaFX GUI Layer
│
├── FXML Views (.fxml)
└── Controllers (.java)
        │
        ▼
Service Layer
│
├── Business Logic
├── Input Validation
└── Workflow Coordination
        │
        ▼
DAO Layer
│
├── SQL Query Execution
├── JDBC Handling
└── ResultSet → Object Mapping
        │
        ▼
MySQL Database
```

---

## 🔐 Authentication Flow

```
MainApp.java
    ↓
login.fxml
    ↓
LoginController  (pass-through only)
    ↓
AuthService      (validates + decides dashboard)
    ↓
UserDAO → MySQL
    ↓
Role Check
    ↓
Admin Dashboard  ──or──  User Dashboard
```

---

## 📂 Project Structure

```
src/com/ims/
│
├── model/
│   ├── Customer.java
│   ├── Product.java
│   ├── Invoice.java
│   ├── InvoiceItem.java
│   └── User.java
│
├── dao/
│   ├── DBConnection.java
│   ├── CustomerDAO.java
│   ├── ProductDAO.java
│   ├── InvoiceDAO.java
│   ├── InvoiceItemDAO.java
│   └── UserDAO.java
│
├── service/
│   ├── CustomerService.java
│   ├── ProductService.java
│   ├── InvoiceService.java
│   └── AuthService.java
│
└── gui/
    ├── MainApp.java
    │
    ├── controller/
    │   ├── LoginController.java
    │   ├── AdminDashboardController.java
    │   ├── UserDashboardController.java
    │   ├── CustomerController.java
    │   ├── ProductController.java
    │   └── InvoiceController.java
    │
    ├── util/
    │   └── SceneNavigator.java
    │
    └── view/
        ├── login.fxml
        ├── admin-dashboard.fxml
        ├── user-dashboard.fxml
        ├── customer-form.fxml
        ├── product-table.fxml
        └── invoice-form.fxml
```

---

## 🗄️ Database Schema

```sql
CREATE DATABASE ims_db;
USE ims_db;

CREATE TABLE users (
    user_id     INT PRIMARY KEY AUTO_INCREMENT,
    username    VARCHAR(50) UNIQUE NOT NULL,
    password    VARCHAR(100) NOT NULL,
    role        ENUM('ADMIN','USER') NOT NULL,
    customer_id INT,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

CREATE TABLE customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(100),
    email       VARCHAR(100) UNIQUE,
    phone       VARCHAR(30),
    address     VARCHAR(255)
);

CREATE TABLE products (
    product_id  INT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(100),
    description VARCHAR(255),
    price       DOUBLE,
    stock_qty   INT
);

CREATE TABLE invoices (
    invoice_id    INT PRIMARY KEY AUTO_INCREMENT,
    customer_id   INT,
    invoice_date  DATE,
    tax_rate      DOUBLE,
    discount_rate DOUBLE,
    total_amount  DOUBLE,
    status        ENUM('PENDING','PAID','CANCELLED'),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

CREATE TABLE invoice_items (
    item_id    INT PRIMARY KEY AUTO_INCREMENT,
    invoice_id INT,
    product_id INT,
    quantity   INT,
    unit_price DOUBLE,
    subtotal   DOUBLE,
    FOREIGN KEY (invoice_id) REFERENCES invoices(invoice_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- Default admin account
INSERT INTO users (username, password, role)
VALUES ('admin', 'admin123', 'ADMIN');
```

---

## 🛠️ Technologies Used

| Layer | Technology |
|-------|------------|
| Language | Java 21 |
| GUI Framework | JavaFX 25 + FXML |
| Database | MySQL 8 |
| DB Driver | MySQL Connector/J |
| Architecture | DAO + Service Layer Pattern |
| IDE | IntelliJ IDEA |

---

## ▶️ How to Run

### Requirements

- Java JDK 21+
- JavaFX SDK 25+
- MySQL 8+
- MySQL Connector/J JAR

---

### Step 1 — Clone Repository

```bash
git clone https://github.com/shahzaib143-szk/IMS.git
cd IMS
```

---

### Step 2 — Set Up Database

Open MySQL Workbench or terminal and run:

```sql
CREATE DATABASE ims_db;
```

Then run the full schema from `schema.sql`.

---

### Step 3 — Configure DBConnection.java

Open `src/com/ims/dao/DBConnection.java` and update:

```java
private static final String URL      = "jdbc:mysql://localhost:3306/ims_db";
private static final String USER     = "root";
private static final String PASSWORD = "your_password_here";
```

---

### Step 4 — Compile Project (Windows)

```cmd
dir /s /b src\*.java > sources.txt

javac -d out ^
  --module-path "C:\javafx-sdk-25.0.3\lib" ^
  --add-modules javafx.controls,javafx.fxml ^
  -cp "lib\mysql-connector-j-8.x.x.jar" ^
  @sources.txt
```

---

### Step 5 — Copy FXML Files to Output

```cmd
xcopy /E /I src\com\ims\gui\view out\com\ims\gui\view
```

---

### Step 6 — Run Application

```cmd
java ^
  --module-path "C:\javafx-sdk-25.0.3\lib" ^
  --add-modules javafx.controls,javafx.fxml ^
  -cp "out;lib\mysql-connector-j-8.x.x.jar" ^
  com.ims.gui.MainApp
```

---

### Default Login Credentials

| Role | Username | Password |
|------|----------|----------|
| Admin | admin | admin123 |

---

## 🧩 Core Modules

| Module | Responsibility |
|--------|---------------|
| Authentication | Login, role verification, session control |
| Customer Management | Full CRUD operations |
| Product Management | Inventory and stock handling |
| Invoice System | Invoice creation and item persistence |
| DAO Layer | Database communication only |
| Service Layer | Business logic and validation |
| JavaFX GUI | User interaction and event handling |

---

## 🧠 OOP Concepts Applied

| Concept | Implementation |
|---------|---------------|
| Classes & Objects | Customer, Product, Invoice, User |
| Encapsulation | Private fields with getters/setters |
| Abstraction | Service and DAO layer separation |
| Composition | Invoice contains List of InvoiceItems |
| Inheritance | Extended constructors across models |
| Exception Handling | Database and input validation |
| Collections Framework | List, Map, ObservableList |

---

## 🔥 Key Engineering Decisions

- **DAO Layer** isolates all SQL logic from business code
- **Service Layer** enforces all business rules and validation
- **Controllers** manage GUI events only — no SQL, no calculations
- **FXML** separates UI design from controller logic
- **Backend is fully reusable** — GUI can be replaced without touching services or DAOs
- **JavaFX replaced console UI** without rewriting any backend code
- **Transaction handling** in InvoiceDAO ensures invoice + items save atomically
- **Snapshot pricing** in InvoiceItem preserves price at time of sale

---

## 📊 Invoice Creation Flow

```
User selects Customer from dropdown
    ↓
User adds Products to cart
    ↓
InvoiceController builds item Map
    ↓
InvoiceService.createInvoice()
    ├── Step 1: Validate customer
    ├── Step 2: Validate products exist
    ├── Step 3: Check stock availability
    ├── Step 4: Build Invoice + InvoiceItems
    ├── Step 5: Calculate total (discount → tax)
    ├── Step 6: Save via InvoiceDAO (transaction)
    └── Step 7: Reduce stock via ProductService
    ↓
Invoice returned to UI
    ↓
Success alert shown
```

---

## 👨‍💻 Author

**Shahzaib Khan**
2nd Semester — Object Oriented Programming Project
Department of Computer Science

---

## 📄 License

This project is developed for academic purposes.
