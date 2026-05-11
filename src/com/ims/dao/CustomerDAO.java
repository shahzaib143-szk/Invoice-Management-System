package com.ims.dao;

import com.ims.model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CustomerDAO {

    private final Connection conn = DBConnection.getConnection();
    public boolean insertCustomer(Customer c) {

        String sql = "INSERT INTO customers (name, email, phone, address) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, c.getName());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getPhone());
            ps.setString(4, c.getAddress());

            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                c.setCustomerId(keys.getInt(1)); 
            }
            return true;

        } catch (SQLException e) {
            System.err.println("[CustomerDAO] insertCustomer failed: " + e.getMessage());
            return false;
        }
    }


    public Customer getCustomerById(int id) {

        String sql = "SELECT * FROM customers WHERE customer_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id); 

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs); 
            }

        } catch (SQLException e) {
            System.err.println("[CustomerDAO] getCustomerById failed: " + e.getMessage());
        }
        return null;
    }

    public Customer getCustomerByEmail(String email) {

        String sql = "SELECT * FROM customers WHERE email = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email); 

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs); 
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

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println("[CustomerDAO] getAllCustomers failed: " + e.getMessage());
        }
        return list;
    }

    public List<Customer> searchByName(String keyword) {

        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE name LIKE ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%"); 

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println("[CustomerDAO] searchByName failed: " + e.getMessage());
        }
        return list;
    }


    public boolean updateCustomer(Customer c) {

        String sql = "UPDATE customers SET name=?, email=?, phone=?, address=? WHERE customer_id=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

             ps.setString(1, c.getName());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getPhone());
            ps.setString(4, c.getAddress());
            ps.setInt(5, c.getCustomerId());

            return ps.executeUpdate() > 0; 

        } catch (SQLException e) {
            System.err.println("[CustomerDAO] updateCustomer failed: " + e.getMessage());
            return false;
        }
    }


    public boolean deleteCustomer(int id) {

        String sql = "DELETE FROM customers WHERE customer_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id); 

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[CustomerDAO] deleteCustomer failed: " + e.getMessage());
            return false;
        }
    }

    
    private Customer mapRow(ResultSet rs) throws SQLException {
        return new Customer(
            rs.getInt("customer_id"),    
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("address")
        );
    }
}
