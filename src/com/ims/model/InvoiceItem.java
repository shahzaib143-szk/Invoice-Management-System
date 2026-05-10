package com.ims.model;

public class InvoiceItem {

    private int     itemId;
    private Invoice invoice;    // parent invoice reference
    private Product product;    // product reference
    private int     quantity;
    private double  unitPrice;
    private double  subtotal;   // quantity * unitPrice

    // --- Constructors ---

    public InvoiceItem() {}

    public InvoiceItem(Product product, int quantity) {
        this.product   = product;
        this.quantity  = quantity;
        this.unitPrice = product.getPrice();
        this.subtotal  = this.unitPrice * quantity;
    }

    // --- Getters & Setters ---

    public int     getItemId()                    { return itemId; }
    public void    setItemId(int id)              { this.itemId = id; }

    public Invoice getInvoice()                   { return invoice; }
    public void    setInvoice(Invoice invoice)    { this.invoice = invoice; }

    public Product getProduct()                   { return product; }
    public void    setProduct(Product product)    { this.product = product; }

    public int     getQuantity()                  { return quantity; }
    public void    setQuantity(int qty)           { this.quantity = qty; }

    public double  getUnitPrice()                 { return unitPrice; }
    public void    setUnitPrice(double price)     { this.unitPrice = price; }

    public double  getSubtotal()                  { return subtotal; }
    public void    setSubtotal(double sub)        { this.subtotal = sub; }

    // Recalculate when quantity or price changes
    public void recalculate() {
        this.subtotal = this.unitPrice * this.quantity;
    }

    @Override
    public String toString() {
        return "InvoiceItem{product=" + product.getName()
                + ", qty=" + quantity + ", subtotal=" + subtotal + "}";
    }
}
