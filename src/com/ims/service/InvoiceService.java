package com.ims.service;

import com.ims.dao.InvoiceDAO;
import com.ims.model.*;
import java.util.List;
import java.util.Map;

/**
 * InvoiceService — the brain of the IMS.
 *
 * Flow: UI → InvoiceService → ProductService (stock check)
 *                           → CustomerService (validate)
 *                           → InvoiceDAO (save to DB)
 */
public class InvoiceService {

    private final InvoiceDAO      invoiceDAO      = new InvoiceDAO();
    private final CustomerService customerService = new CustomerService();
    private final ProductService  productService  = new ProductService();

    /**
     * Create a full invoice.
     *
     * @param customerId   the customer placing the order
     * @param items        Map of productId → quantity requested
     * @param taxRate      e.g. 0.10 for 10%
     * @param discountRate e.g. 0.05 for 5%
     * @return the saved Invoice, or null on failure
     */
    public Invoice createInvoice(int customerId,
                                 Map<Integer, Integer> items,
                                 double taxRate,
                                 double discountRate) {

        // 1. Validate customer
        Customer customer = customerService.findById(customerId);
        if (customer == null) {
            System.err.println("[InvoiceService] Customer not found: " + customerId);
            return null;
        }

        // 2. Build invoice shell
        Invoice invoice = new Invoice(customer, taxRate, discountRate);

        // 3. Validate stock & build line items
        for (Map.Entry<Integer, Integer> entry : items.entrySet()) {
            int productId = entry.getKey();
            int qty       = entry.getValue();

            if (!productService.isInStock(productId, qty)) {
                System.err.println("[InvoiceService] Insufficient stock for product: " + productId);
                return null;
            }

            Product product = productService.getProductById(productId);
            invoice.addItem(new InvoiceItem(product, qty));
        }

        // 4. Calculate totals
        double total = calculateTotal(invoice.getItems(), taxRate, discountRate);
        invoice.setTotalAmount(total);

        // 5. Save to DB
        boolean saved = invoiceDAO.createInvoice(invoice);
        if (!saved) return null;

        // 6. Reduce stock for each item sold
        for (InvoiceItem item : invoice.getItems()) {
            productService.reduceStock(item.getProduct().getProductId(), item.getQuantity());
        }

        return invoice;
    }

    // ── Calculation methods (replaces your C++ compute functions) ─────────────

    /** Sum of all line item subtotals */
    public double calculateSubtotal(List<InvoiceItem> items) {
        double subtotal = 0;
        for (InvoiceItem item : items) {
            subtotal += item.getSubtotal();
        }
        return subtotal;
    }

    /** Apply tax on top of subtotal */
    public double applyTax(double subtotal, double taxRate) {
        return subtotal * (1 + taxRate);
    }

    /** Apply discount — discount is applied BEFORE tax */
    public double applyDiscount(double subtotal, double discountRate) {
        return subtotal * (1 - discountRate);
    }

    /**
     * Full total: subtotal → discount → tax
     * Formula: subtotal * (1 - discount) * (1 + tax)
     */
    public double calculateTotal(List<InvoiceItem> items, double taxRate, double discountRate) {
        double subtotal    = calculateSubtotal(items);
        double afterDisc   = applyDiscount(subtotal, discountRate);
        double finalTotal  = applyTax(afterDisc, taxRate);
        return Math.round(finalTotal * 100.0) / 100.0; // Round to 2 decimals
    }

    // ── Retrieval ─────────────────────────────────────────────────────────────

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
