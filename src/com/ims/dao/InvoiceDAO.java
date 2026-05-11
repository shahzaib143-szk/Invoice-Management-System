package com.ims.dao;

import com.ims.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {

    private final Connection conn = DBConnection.getConnection();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final InvoiceItemDAO invoiceItemDAO = new InvoiceItemDAO();

    public boolean createInvoice(Invoice inv) {
        String sql = "INSERT INTO invoices (customer_id, invoice_date, tax_rate, discount_rate, total_amount, status) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, inv.getCustomer().getCustomerId());
                ps.setDate(2, Date.valueOf(inv.getInvoiceDate()));
                ps.setDouble(3, inv.getTaxRate());
                ps.setDouble(4, inv.getDiscountRate());
                ps.setDouble(5, inv.getTotalAmount());
                ps.setString(6, inv.getStatus().name());

                ps.executeUpdate();

                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    int invoiceId = keys.getInt(1);
                    inv.setInvoiceId(invoiceId);

                    boolean itemsSaved = invoiceItemDAO.insertItems(invoiceId, inv.getItems());
                    if (!itemsSaved) {
                        conn.rollback();
                        return false;
                    }
                }

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                return false;
            }

        } catch (SQLException e) {
            return false;

        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                // ignore
            }
        }
    }

    public Invoice getInvoiceById(int id) {
        String sql = "SELECT * FROM invoices WHERE invoice_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Invoice inv = mapRow(rs);
                inv.setItems(invoiceItemDAO.getItemsByInvoiceId(id));
                return inv;
            }

        } catch (SQLException e) {
            // ignore
        }
        return null;
    }

    public List<Invoice> getAllInvoices() {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT * FROM invoices ORDER BY invoice_date DESC";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            // ignore
        }
        return list;
    }

    public List<String> getCustomerInvoiceReport() {
        List<String> report = new ArrayList<>();
        String sql = "SELECT c.name, i.total_amount, i.invoice_date FROM customers c JOIN invoices i ON c.customer_id = i.customer_id ORDER BY i.invoice_date DESC";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String line = "Customer: " + rs.getString("name") + " | Total: " + rs.getDouble("total_amount") + " | Date: " + rs.getDate("invoice_date");
                report.add(line);
            }
        } catch (SQLException e) {
            // ignore
        }
        return report;
    }

    public boolean updateStatus(int invoiceId, Invoice.Status status) {
        String sql = "UPDATE invoices SET status = ? WHERE invoice_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, invoiceId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    private Invoice mapRow(ResultSet rs) throws SQLException {
        Customer customer = customerDAO.getCustomerById(rs.getInt("customer_id"));

        Invoice inv = new Invoice(customer, rs.getDouble("tax_rate"), rs.getDouble("discount_rate"));
        inv.setInvoiceId(rs.getInt("invoice_id"));
        inv.setInvoiceDate(rs.getDate("invoice_date").toLocalDate());
        inv.setTotalAmount(rs.getDouble("total_amount"));
        inv.setStatus(Invoice.Status.valueOf(rs.getString("status")));

        return inv;
    }
}
