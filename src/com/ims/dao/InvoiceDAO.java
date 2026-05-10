package com.ims.dao;

import com.ims.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * InvoiceDAO
 *
 * DAO Rule: ONLY SQL + ResultSet mapping. Zero business logic here.
 *
 * Concepts demonstrated:
 *   PreparedStatement → every SQL uses ? placeholders
 *   CRUD             → insert, select, update all present
 *   Generated keys   → auto-generated invoice_id fed back into object
 *   Object mapping   → mapRow() converts ResultSet → Invoice
 *   Relationships    → invoice links to customer (FK) and items (FK)
 *                      InvoiceItemDAO called inside createInvoice
 *   Transaction      → invoice + items saved together or not at all
 */
public class InvoiceDAO {

    private final Connection     conn           = DBConnection.getConnection();
    private final CustomerDAO    customerDAO    = new CustomerDAO();
    private final InvoiceItemDAO invoiceItemDAO = new InvoiceItemDAO();


    // ═════════════════════════════════════════════════════════════════════════
    // CREATE — INSERT with TRANSACTION
    //
    // Relationship handled here:
    //   Invoice (parent) → saved first → gets invoice_id
    //   InvoiceItems (children) → saved after using that invoice_id
    //   FK: invoice_items.invoice_id → invoices.invoice_id
    //
    // Transaction: both invoice and items saved together
    //   If items fail → invoice insert is rolled back
    //   Either both save or neither saves
    // ═════════════════════════════════════════════════════════════════════════

    public boolean createInvoice(Invoice inv) {

        String sql = "INSERT INTO invoices "
                   + "(customer_id, invoice_date, tax_rate, discount_rate, total_amount, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try {
            // START TRANSACTION — disable auto-commit
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                // PreparedStatement — FK customer_id linked here
                ps.setInt(1, inv.getCustomer().getCustomerId()); // FK → customers table
                ps.setDate(2, Date.valueOf(inv.getInvoiceDate()));
                ps.setDouble(3, inv.getTaxRate());
                ps.setDouble(4, inv.getDiscountRate());
                ps.setDouble(5, inv.getTotalAmount());
                ps.setString(6, inv.getStatus().name());

                ps.executeUpdate();

                // Generated key — read auto-generated invoice_id
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    int invoiceId = keys.getInt(1);
                    inv.setInvoiceId(invoiceId); // feed back into object

                    // Relationship — save child items using parent invoice_id
                    boolean itemsSaved = invoiceItemDAO.insertItems(invoiceId, inv.getItems());

                    if (!itemsSaved) {
                        conn.rollback(); // ROLLBACK — items failed → undo invoice
                        System.err.println("[InvoiceDAO] Items save failed. Invoice rolled back.");
                        return false;
                    }
                }

                conn.commit(); // COMMIT — both invoice and items saved
                return true;

            } catch (SQLException e) {
                conn.rollback(); // ROLLBACK — any SQL error → undo everything
                System.err.println("[InvoiceDAO] createInvoice rolled back: " + e.getMessage());
                return false;
            }

        } catch (SQLException e) {
            System.err.println("[InvoiceDAO] Transaction setup failed: " + e.getMessage());
            return false;

        } finally {
            try {
                conn.setAutoCommit(true); // always restore auto-commit
            } catch (SQLException e) {
                System.err.println("[InvoiceDAO] Could not restore auto-commit.");
            }
        }
    }


    // ═════════════════════════════════════════════════════════════════════════
    // READ — SELECT
    // Relationship → invoice fetched → then items attached → then product in each item
    // This reconstructs the full object graph from 3 tables
    // ═════════════════════════════════════════════════════════════════════════

    public Invoice getInvoiceById(int id) {

        String sql = "SELECT * FROM invoices WHERE invoice_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id); // PreparedStatement — safe ID

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Invoice inv = mapRow(rs); // Object mapping → Invoice built

                // Relationship → attach child items to parent invoice
                inv.setItems(invoiceItemDAO.getItemsByInvoiceId(id));

                return inv;
            }

        } catch (SQLException e) {
            System.err.println("[InvoiceDAO] getInvoiceById failed: " + e.getMessage());
        }
        return null;
    }

    public List<Invoice> getAllInvoices() {

        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT * FROM invoices ORDER BY invoice_date DESC";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs)); // Object mapping — each row → Invoice
            }

        } catch (SQLException e) {
            System.err.println("[InvoiceDAO] getAllInvoices failed: " + e.getMessage());
        }
        return list;
    }

    // Customer invoice report — for slide 21 (JOIN query)
    public List<String> getCustomerInvoiceReport() {

        List<String> report = new ArrayList<>();

        // JOIN query — invoices joined with customers
        String sql = "SELECT c.name, i.total_amount, i.invoice_date "
                   + "FROM customers c "
                   + "JOIN invoices i ON c.customer_id = i.customer_id "
                   + "ORDER BY i.invoice_date DESC";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String line = "Customer: " + rs.getString("name")
                            + " | Total: "  + rs.getDouble("total_amount")
                            + " | Date: "   + rs.getDate("invoice_date");
                report.add(line);
            }

        } catch (SQLException e) {
            System.err.println("[InvoiceDAO] customerReport failed: " + e.getMessage());
        }
        return report;
    }


    // ═════════════════════════════════════════════════════════════════════════
    // UPDATE
    // PreparedStatement → status and ID passed safely
    // ═════════════════════════════════════════════════════════════════════════

    public boolean updateStatus(int invoiceId, Invoice.Status status) {

        String sql = "UPDATE invoices SET status = ? WHERE invoice_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name()); // PreparedStatement
            ps.setInt(2, invoiceId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[InvoiceDAO] updateStatus failed: " + e.getMessage());
            return false;
        }
    }


    // ═════════════════════════════════════════════════════════════════════════
    // OBJECT MAPPING
    // Converts one ResultSet row → Invoice object
    // FK relationship → customer_id used to fetch full Customer object
    // ═════════════════════════════════════════════════════════════════════════

    private Invoice mapRow(ResultSet rs) throws SQLException {

        // Relationship — use FK customer_id to fetch full Customer object
        Customer customer = customerDAO.getCustomerById(rs.getInt("customer_id"));

        Invoice inv = new Invoice(customer,
                                  rs.getDouble("tax_rate"),
                                  rs.getDouble("discount_rate"));

        inv.setInvoiceId(rs.getInt("invoice_id"));
        inv.setInvoiceDate(rs.getDate("invoice_date").toLocalDate());
        inv.setTotalAmount(rs.getDouble("total_amount"));
        inv.setStatus(Invoice.Status.valueOf(rs.getString("status")));

        return inv;
    }
}
