package com.ims.model;

public class Product {

    private int    productId;
    private String name;
    private String description;
    private double price;
    private int    stockQty;

    // --- Constructors ---

    public Product() {}

    public Product(String name, String description, double price, int stockQty) {
        this.name        = name;
        this.description = description;
        this.price       = price;
        this.stockQty    = stockQty;
    }

    public Product(int productId, String name, String description, double price, int stockQty) {
        this.productId   = productId;
        this.name        = name;
        this.description = description;
        this.price       = price;
        this.stockQty    = stockQty;
    }

    // --- Getters & Setters ---

    public int    getProductId()                  { return productId; }
    public void   setProductId(int id)            { this.productId = id; }

    public String getName()                       { return name; }
    public void   setName(String name)            { this.name = name; }

    public String getDescription()                { return description; }
    public void   setDescription(String desc)     { this.description = desc; }

    public double getPrice()                      { return price; }
    public void   setPrice(double price)          { this.price = price; }

    public int    getStockQty()                   { return stockQty; }
    public void   setStockQty(int qty)            { this.stockQty = qty; }

    // Convenience: reduce stock after sale
    public void reduceStock(int qty) {
        this.stockQty -= qty;
    }

    @Override
    public String toString() {
        return "Product{id=" + productId + ", name='" + name
                + "', price=" + price + ", stock=" + stockQty + "}";
    }
}
