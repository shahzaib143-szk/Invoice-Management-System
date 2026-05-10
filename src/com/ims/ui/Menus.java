package com.ims.ui;

import com.ims.service.*;
import com.ims.model.*;
import java.util.*;

// ─────────────────────────────────────────────────────────────────────────────
// UI Rule: input/output ONLY. No logic. No SQL. Just call services.
// ─────────────────────────────────────────────────────────────────────────────

class CustomerMenu {
    private final Scanner         sc  = new Scanner(System.in);
    private final CustomerService svc = new CustomerService();

    void show() {
        int choice;
        do {
            System.out.println("\n--- Customer Menu ---");
            System.out.println("1. Add Customer");
            System.out.println("2. View All Customers");
            System.out.println("0. Back");
            System.out.print("Choice: ");
            choice = Integer.parseInt(sc.nextLine().trim());

            switch (choice) {
                case 1 -> addCustomer();
                case 2 -> viewAll();
            }
        } while (choice != 0);
    }

    private void addCustomer() {
        System.out.print("Name: ");   String name    = sc.nextLine();
        System.out.print("Email: ");  String email   = sc.nextLine();
        System.out.print("Phone: ");  String phone   = sc.nextLine();
        System.out.print("Address: ");String address = sc.nextLine();

        boolean ok = svc.addCustomer(name, email, phone, address);
        System.out.println(ok ? "✅ Customer added." : "❌ Failed to add customer.");
    }

    private void viewAll() {
        List<Customer> list = svc.getAllCustomers();
        if (list.isEmpty()) { System.out.println("No customers found."); return; }
        list.forEach(System.out::println);
    }
}

// ─────────────────────────────────────────────────────────────────────────────

class ProductMenu {
    private final Scanner        sc  = new Scanner(System.in);
    private final ProductService svc = new ProductService();

    void show() {
        int choice;
        do {
            System.out.println("\n--- Product Menu ---");
            System.out.println("1. Add Product");
            System.out.println("2. View All Products");
            System.out.println("3. Search by Name");
            System.out.println("0. Back");
            System.out.print("Choice: ");
            choice = Integer.parseInt(sc.nextLine().trim());

            switch (choice) {
                case 1 -> addProduct();
                case 2 -> viewAll();
                case 3 -> search();
            }
        } while (choice != 0);
    }

    private void addProduct() {
        System.out.print("Name: ");        String name = sc.nextLine();
        System.out.print("Description: "); String desc = sc.nextLine();
        System.out.print("Price: ");       double price = Double.parseDouble(sc.nextLine());
        System.out.print("Stock Qty: ");   int qty = Integer.parseInt(sc.nextLine());

        boolean ok = svc.addProduct(name, desc, price, qty);
        System.out.println(ok ? "✅ Product added." : "❌ Failed.");
    }

    private void viewAll() {
        List<Product> list = svc.getAllProducts();
        if (list.isEmpty()) { System.out.println("No products found."); return; }
        list.forEach(System.out::println);
    }

    private void search() {
        System.out.print("Keyword: ");
        String kw = sc.nextLine();
        svc.searchByName(kw).forEach(System.out::println);
    }
}

// ─────────────────────────────────────────────────────────────────────────────

class InvoiceMenu {
    private final Scanner         sc  = new Scanner(System.in);
    private final InvoiceService  svc = new InvoiceService();

    void show() {
        int choice;
        do {
            System.out.println("\n--- Invoice Menu ---");
            System.out.println("1. Create Invoice");
            System.out.println("2. View Invoice by ID");
            System.out.println("3. View All Invoices");
            System.out.println("4. Update Invoice Status");
            System.out.println("0. Back");
            System.out.print("Choice: ");
            choice = Integer.parseInt(sc.nextLine().trim());

            switch (choice) {
                case 1 -> createInvoice();
                case 2 -> viewById();
                case 3 -> viewAll();
                case 4 -> updateStatus();
            }
        } while (choice != 0);
    }

    private void createInvoice() {
        System.out.print("Customer ID: ");
        int customerId = Integer.parseInt(sc.nextLine());

        Map<Integer, Integer> items = new LinkedHashMap<>();
        System.out.println("Enter products (product ID + quantity). Type 0 to finish.");
        while (true) {
            System.out.print("Product ID (0 to stop): ");
            int pid = Integer.parseInt(sc.nextLine());
            if (pid == 0) break;
            System.out.print("Quantity: ");
            int qty = Integer.parseInt(sc.nextLine());
            items.put(pid, qty);
        }

        System.out.print("Tax rate (e.g. 0.10): ");
        double tax = Double.parseDouble(sc.nextLine());
        System.out.print("Discount rate (e.g. 0.05): ");
        double disc = Double.parseDouble(sc.nextLine());

        Invoice inv = svc.createInvoice(customerId, items, tax, disc);
        if (inv != null)
            System.out.println("✅ Invoice created: " + inv);
        else
            System.out.println("❌ Invoice creation failed.");
    }

    private void viewById() {
        System.out.print("Invoice ID: ");
        int id = Integer.parseInt(sc.nextLine());
        Invoice inv = svc.getInvoiceById(id);
        if (inv == null) { System.out.println("Not found."); return; }
        System.out.println(inv);
        inv.getItems().forEach(i -> System.out.println("  " + i));
    }

    private void viewAll() {
        svc.getAllInvoices().forEach(System.out::println);
    }

    private void updateStatus() {
        System.out.print("Invoice ID: ");
        int id = Integer.parseInt(sc.nextLine());
        System.out.println("Status options: PENDING, PAID, CANCELLED");
        System.out.print("New status: ");
        String statusStr = sc.nextLine().toUpperCase();
        Invoice.Status status = Invoice.Status.valueOf(statusStr);
        boolean ok = svc.updateInvoiceStatus(id, status);
        System.out.println(ok ? "✅ Status updated." : "❌ Update failed.");
    }
}
