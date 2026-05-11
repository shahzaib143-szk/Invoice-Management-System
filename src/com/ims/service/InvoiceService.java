package com.ims.service;

import com.ims.dao.InvoiceDAO;
import com.ims.model.*;
import java.util.List;
import java.util.Map;

public class InvoiceService {

    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final CustomerService customerService = new CustomerService();
    private final ProductService productService = new ProductService();

    public Invoice createInvoice(int customerId,
                                 Map<Integer, Integer> items,
                                 double taxRate,
                                 double discountRate) {

        Customer customer = customerService.findById(customerId);
        if (customer == null) {
            return null;
        }

        Invoice invoice = new Invoice(customer, taxRate, discountRate);

        for (Map.Entry<Integer, Integer> entry : items.entrySet()) {
            int productId = entry.getKey();
            int qty = entry.getValue();

            if (!productService.isInStock(productId, qty)) {
                return null;
            }

            Product product = productService.getProductById(productId);
            invoice.addItem(new InvoiceItem(product, qty));
        }

        double total = calculateTotal(invoice.getItems(), taxRate, discountRate);
        invoice.setTotalAmount(total);

        boolean saved = invoiceDAO.createInvoice(invoice);
        if (!saved) return null;

        for (InvoiceItem item : invoice.getItems()) {
            productService.reduceStock(item.getProduct().getProductId(), item.getQuantity());
        }

        return invoice;
    }

    public double calculateSubtotal(List<InvoiceItem> items) {
        double subtotal = 0;
        for (InvoiceItem item : items) {
            subtotal += item.getSubtotal();
        }
        return subtotal;
    }

    public double applyTax(double subtotal, double taxRate) {
        return subtotal * (1 + taxRate);
    }

    public double applyDiscount(double subtotal, double discountRate) {
        return subtotal * (1 - discountRate);
    }

    public double calculateTotal(List<InvoiceItem> items, double taxRate, double discountRate) {
        double subtotal = calculateSubtotal(items);
        double afterDisc = applyDiscount(subtotal, discountRate);
        double finalTotal = applyTax(afterDisc, taxRate);
        return Math.round(finalTotal * 100.0) / 100.0;
    }

    public Invoice getInvoiceById(int id) {
        return invoiceDAO.getInvoiceById(id);
    }

    public List<Invoice> getAllInvoices() {
        return invoiceDAO.getAllInvoices();
    }

    public boolean updateInvoiceStatus(int invoiceId, Invoice.Status status) {
        return invoiceDAO.updateStatus(invoiceId, status);
    }
}
