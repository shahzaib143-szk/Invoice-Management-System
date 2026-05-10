package com.ims.dao;

import com.ims.model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CustomerDAO
 *
 * DAO Rule: ONLY SQL + ResultSet mapping. Zero business logic here.
 *
 * Concepts demonstrated:
 *   PreparedStatement → used in every method (safe SQL)
 *   CRUD             → insert, select, update, delete all present
 *   Generated keys   → auto-generated customer_id fed back into object
 *   Object mapping   → mapRow() converts ResultSet → Customer
 */
public class CustomerDAO {

    // ── Single shared DB connection ───────────────────────────────────────────
    private final Connection conn = DBConnection.getConnection();


    // ═════════════════════════════════════════════════════════════════════════
    // CREATE — INSERT
    // PreparedStatement used → ? placeholders → safe from SQL injection
    // Generated key → DB creates customer_id → we read it back → set in object
    // ═════════════════════════════════════════════════════════════════════════

    public boolean insertCustomer(Customer c) {

        // PreparedStatement with RETURN_GENERATED_KEYS flag
        String sql = "INSERT INTO customers (name, email, phone, address) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // PreparedStatement — set each ? parameter safely
            ps.setString(1, c.getName());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getPhone());
            ps.setString(4, c.getAddress());

            ps.executeUpdate();

            // Generated key — read auto-generated customer_id from DB
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                c.setCustomerId(keys.getInt(1)); // feed ID back into object
            }

            return true;

        } catch (SQLException e) {
            System.err.println("[CustomerDAO] insertCustomer failed: " + e.getMessage());
            return false;
        }
    }


    // ═════════════════════════════════════════════════════════════════════════
    // READ — SELECT
    // PreparedStatement used → parameter passed safely via setInt / setString
    // Object mapping → mapRow() converts each ResultSet row → Customer object
    // ═════════════════════════════════════════════════════════════════════════

    public Customer getCustomerById(int id) {

        String sql = "SELECT * FROM customers WHERE customer_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id); // PreparedStatement — safe parameter

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs); // Object mapping — row → Customer
            }

        } catch (SQLException e) {
            System.err.println("[CustomerDAO] getCustomerById failed: " + e.getMessage());
        }
        return null;
    }

    public Customer getCustomerByEmail(String email) {

        String sql = "SELECT * FROM customers WHERE email = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email); // PreparedStatement — safe string parameter

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs); // Object mapping
            }

        } catch (SQLException e) {
            System.err.println("[CustomerDAO] getCustomerByEmail failed: " + e.getMessage());
        }
        return null;
    }

    public List<Customer> getAllCustomers() {

        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY name";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            // Object mapping — every row becomes a Customer object
            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println("[CustomerDAO] getAllCustomers failed: " + e.getMessage());
        }
        return list;
    }

    // Search by name using LIKE — for slide 19 (LIKE query)
    public List<Customer> searchByName(String keyword) {

        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE name LIKE ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%"); // PreparedStatement with LIKE

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println("[CustomerDAO] searchByName failed: " + e.getMessage());
        }
        return list;
    }


    // ═════════════════════════════════════════════════════════════════════════
    // UPDATE
    // PreparedStatement → all fields set safely via parameters
    // ═════════════════════════════════════════════════════════════════════════

    public boolean updateCustomer(Customer c) {

        String sql = "UPDATE customers SET name=?, email=?, phone=?, address=? WHERE customer_id=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            // PreparedStatement — set each field safely
            ps.setString(1, c.getName());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getPhone());
            ps.setString(4, c.getAddress());
            ps.setInt(5, c.getCustomerId());

            return ps.executeUpdate() > 0; // returns true if row was updated

        } catch (SQLException e) {
            System.err.println("[CustomerDAO] updateCustomer failed: " + e.getMessage());
            return false;
        }
    }


    // ═════════════════════════════════════════════════════════════════════════
    // DELETE
    // PreparedStatement → ID passed safely
    // ═════════════════════════════════════════════════════════════════════════

    public boolean deleteCustomer(int id) {

        String sql = "DELETE FROM customers WHERE customer_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id); // PreparedStatement — safe ID parameter

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[CustomerDAO] deleteCustomer failed: " + e.getMessage());
            return false;
        }
    }


    // ═════════════════════════════════════════════════════════════════════════
    // OBJECT MAPPING — private helper
    // Converts one ResultSet row → one Customer object
    // Called by every SELECT method above
    // ═════════════════════════════════════════════════════════════════════════

    private Customer mapRow(ResultSet rs) throws SQLException {
        return new Customer(
            rs.getInt("customer_id"),    // PK from DB
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("address")
        );
    }
}
