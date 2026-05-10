package com.ims.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Invoice {

    public enum Status { PENDING, PAID, CANCELLED }

    private int         invoiceId;
    private Customer    customer;
    private LocalDate   invoiceDate;
    private double      taxRate;        // e.g. 0.10 = 10%
    private double      discountRate;   // e.g. 0.05 = 5%
    private double      totalAmount;    // calculated, not set manually
    private Status      status;
    private List<InvoiceItem> items;

    // --- Constructors ---

    public Invoice() {
        this.items       = new ArrayList<>();
        this.invoiceDate = LocalDate.now();
        this.status      = Status.PENDING;
    }

    public Invoice(Customer customer, double taxRate, double discountRate) {
        this();
        this.customer     = customer;
        this.taxRate      = taxRate;
        this.discountRate = discountRate;
    }

    // --- Getters & Setters ---

    public int          getInvoiceId()                     { return invoiceId; }
    public void         setInvoiceId(int id)               { this.invoiceId = id; }

    public Customer     getCustomer()                      { return customer; }
    public void         setCustomer(Customer customer)     { this.customer = customer; }

    public LocalDate    getInvoiceDate()                   { return invoiceDate; }
    public void         setInvoiceDate(LocalDate date)     { this.invoiceDate = date; }

    public double       getTaxRate()                       { return taxRate; }
    public void         setTaxRate(double taxRate)         { this.taxRate = taxRate; }

    public double       getDiscountRate()                  { return discountRate; }
    public void         setDiscountRate(double rate)       { this.discountRate = rate; }

    public double       getTotalAmount()                   { return totalAmount; }
    public void         setTotalAmount(double total)       { this.totalAmount = total; }

    public Status       getStatus()                        { return status; }
    public void         setStatus(Status status)           { this.status = status; }

    public List<InvoiceItem> getItems()                    { return items; }
    public void              setItems(List<InvoiceItem> i) { this.items = i; }

    public void addItem(InvoiceItem item) {
        this.items.add(item);
    }

    @Override
    public String toString() {
        return "Invoice{id=" + invoiceId + ", customer=" + customer.getName()
                + ", total=" + totalAmount + ", status=" + status + "}";
    }
}
