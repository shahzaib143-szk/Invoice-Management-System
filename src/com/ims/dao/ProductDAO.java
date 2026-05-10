package com.ims.dao;

import com.ims.model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ProductDAO
 *
 * DAO Rule: ONLY SQL + ResultSet mapping. Zero business logic here.
 *
 * Concepts demonstrated:
 *   PreparedStatement → every SQL uses ? placeholders
 *   CRUD             → insert, select, update all present
 *   Generated keys   → auto-generated product_id fed back into object
 *   Object mapping   → mapRow() converts ResultSet → Product
 */
public class ProductDAO {

    private final Connection conn = DBConnection.getConnection();


    // ═════════════════════════════════════════════════════════════════════════
    // CREATE — INSERT
    // PreparedStatement → safe SQL
    // Generated key → product_id created by DB → fed back into Product object
    // ═════════════════════════════════════════════════════════════════════════

    public boolean insertProduct(Product p) {

        String sql = "INSERT INTO products (name, description, price, stock_qty) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // PreparedStatement — each ? set safely
            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setDouble(3, p.getPrice());
            ps.setInt(4, p.getStockQty());

            ps.executeUpdate();

            // Generated key — read auto-generated product_id
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                p.setProductId(keys.getInt(1)); // feed back into object
            }

            return true;

        } catch (SQLException e) {
            System.err.println("[ProductDAO] insertProduct failed: " + e.getMessage());
            return false;
        }
    }


    // ═════════════════════════════════════════════════════════════════════════
    // READ — SELECT
    // PreparedStatement → parameter passed safely
    // Object mapping → mapRow() converts row → Product
    // ═════════════════════════════════════════════════════════════════════════

    public Product getProductById(int id) {

        String sql = "SELECT * FROM products WHERE product_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id); // PreparedStatement — safe ID

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs); // Object mapping
            }

        } catch (SQLException e) {
            System.err.println("[ProductDAO] getProductById failed: " + e.getMessage());
        }
        return null;
    }

    public List<Product> getAllProducts() {

        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY name";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs)); // Object mapping — every row → Product
            }

        } catch (SQLException e) {
            System.err.println("[ProductDAO] getAllProducts failed: " + e.getMessage());
        }
        return list;
    }

    // ORDER BY price — for slide 19 (ORDER BY query)
    public List<Product> getAllProductsSortedByPrice() {

        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY price ASC";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println("[ProductDAO] sortByPrice failed: " + e.getMessage());
        }
        return list;
    }

    // LIKE search — for slide 19 (LIKE query)
    public List<Product> searchByName(String keyword) {

        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE name LIKE ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%"); // PreparedStatement with LIKE

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println("[ProductDAO] searchByName failed: " + e.getMessage());
        }
        return list;
    }

    // Low stock report — for slide 21
    public List<Product> getLowStockProducts() {

        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE stock_qty < 10 ORDER BY stock_qty ASC";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println("[ProductDAO] getLowStock failed: " + e.getMessage());
        }
        return list;
    }


    // ═════════════════════════════════════════════════════════════════════════
    // UPDATE
    // PreparedStatement → fields set safely
    // ═════════════════════════════════════════════════════════════════════════

    public boolean updatePrice(int productId, double newPrice) {

        String sql = "UPDATE products SET price = ? WHERE product_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, newPrice);  // PreparedStatement
            ps.setInt(2, productId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[ProductDAO] updatePrice failed: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStock(int productId, int newQty) {

        String sql = "UPDATE products SET stock_qty = ? WHERE product_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, newQty);      // PreparedStatement
            ps.setInt(2, productId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[ProductDAO] updateStock failed: " + e.getMessage());
            return false;
        }
    }

    // checkStock returns 0 instead of -1 when product not found (safer)
    public int checkStock(int productId) {

        String sql = "SELECT stock_qty FROM products WHERE product_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId); // PreparedStatement

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("stock_qty"); // Object mapping — single value
            }

        } catch (SQLException e) {
            System.err.println("[ProductDAO] checkStock failed: " + e.getMessage());
        }
        return 0; // 0 = not found or no stock (safer than -1)
    }


    // ═════════════════════════════════════════════════════════════════════════
    // OBJECT MAPPING
    // Converts one ResultSet row → one Product object
    // ═════════════════════════════════════════════════════════════════════════

    private Product mapRow(ResultSet rs) throws SQLException {
        return new Product(
            rs.getInt("product_id"),       // PK from DB
            rs.getString("name"),
            rs.getString("description"),
            rs.getDouble("price"),
            rs.getInt("stock_qty")
        );
    }
}
