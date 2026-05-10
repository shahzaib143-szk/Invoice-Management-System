package com.ims.dao;

import com.ims.model.InvoiceItem;
import com.ims.model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * InvoiceItemDAO
 *
 * DAO Rule: ONLY SQL + ResultSet mapping. Zero business logic here.
 *
 * Concepts demonstrated:
 *   PreparedStatement → every SQL uses ? placeholders
 *   Batch insert      → all items inserted in one DB call (efficient)
 *   Generated keys    → item_id read back after batch insert
 *   Object mapping    → ResultSet rows → InvoiceItem objects
 *   Relationships     → item links to invoice (FK) and product (FK)
 *                       ProductDAO called to reconstruct Product object
 */
public class InvoiceItemDAO {

    private final Connection conn       = DBConnection.getConnection();
    private final ProductDAO productDAO = new ProductDAO();


    // ═════════════════════════════════════════════════════════════════════════
    // BATCH INSERT — all items for one invoice saved in one DB call
    //
    // Why batch insert:
    //   One invoice can have 5, 10, 20 products
    //   Sending one INSERT per product = slow (many DB round trips)
    //   Batch insert = all rows sent together = fast (one DB round trip)
    //
    // Relationship:
    //   FK: invoice_items.invoice_id → invoices.invoice_id
    //   FK: invoice_items.product_id → products.product_id
    // ═════════════════════════════════════════════════════════════════════════

    public boolean insertItems(int invoiceId, List<InvoiceItem> items) {

        String sql = "INSERT INTO invoice_items "
                   + "(invoice_id, product_id, quantity, unit_price, subtotal) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Build batch — add each item as one batch entry
            for (InvoiceItem item : items) {

                // PreparedStatement — each ? set safely
                ps.setInt(1, invoiceId);                          // FK → invoices
                ps.setInt(2, item.getProduct().getProductId());   // FK → products
                ps.setInt(3, item.getQuantity());
                ps.setDouble(4, item.getUnitPrice());             // snapshot price
                ps.setDouble(5, item.getSubtotal());

                ps.addBatch(); // add this row to the batch
            }

            // Execute all rows at once — one DB call for all items
            int[] results = ps.executeBatch();

            // Generated keys — read back item_ids for each inserted row
            ResultSet keys = ps.getGeneratedKeys();
            int index = 0;
            while (keys.next() && index < items.size()) {
                items.get(index).setItemId(keys.getInt(1)); // feed ID back
                index++;
            }

            // Verify all rows were inserted
            for (int result : results) {
                if (result == Statement.EXECUTE_FAILED) {
                    System.err.println("[InvoiceItemDAO] One or more items failed to insert.");
                    return false;
                }
            }

            return true;

        } catch (SQLException e) {
            System.err.println("[InvoiceItemDAO] insertItems failed: " + e.getMessage());
            return false;
        }
    }


    // ═════════════════════════════════════════════════════════════════════════
    // READ — SELECT all items for one invoice
    //
    // Object mapping → each row → InvoiceItem object
    // Relationship   → product_id FK used to fetch full Product object
    //                  This reconstructs the full object from 2 tables
    // ═════════════════════════════════════════════════════════════════════════

    public List<InvoiceItem> getItemsByInvoiceId(int invoiceId) {

        List<InvoiceItem> list = new ArrayList<>();
        String sql = "SELECT * FROM invoice_items WHERE invoice_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, invoiceId); // PreparedStatement — safe FK lookup

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                // Object mapping — build InvoiceItem from row
                InvoiceItem item = new InvoiceItem();
                item.setItemId(rs.getInt("item_id"));
                item.setQuantity(rs.getInt("quantity"));
                item.setUnitPrice(rs.getDouble("unit_price")); // stored snapshot price
                item.setSubtotal(rs.getDouble("subtotal"));

                // Relationship — use FK product_id to fetch full Product object
                int productId = rs.getInt("product_id");
                Product product = productDAO.getProductById(productId);
                item.setProduct(product); // attach Product to InvoiceItem

                list.add(item);
            }

        } catch (SQLException e) {
            System.err.println("[InvoiceItemDAO] getItemsByInvoiceId failed: " + e.getMessage());
        }
        return list;
    }
}
