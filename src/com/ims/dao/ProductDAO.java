```java
package com.ims.dao;

import com.ims.model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    private final Connection conn = DBConnection.getConnection();

    public boolean insertProduct(Product p) {
        String sql = "INSERT INTO products (name, description, price, stock_qty) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setDouble(3, p.getPrice());
            ps.setInt(4, p.getStockQty());

            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                p.setProductId(keys.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public Product getProductById(int id) {
        String sql = "SELECT * FROM products WHERE product_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            // ignore
        }
        return null;
    }

    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY name";

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

    public List<Product> getAllProductsSortedByPrice() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY price ASC";

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

    public List<Product> searchByName(String keyword) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE name LIKE ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            // ignore
        }
        return list;
    }

    public List<Product> getLowStockProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE stock_qty < 10 ORDER BY stock_qty ASC";

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

    public boolean updatePrice(int productId, double newPrice) {
        String sql = "UPDATE products SET price = ? WHERE product_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newPrice);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean updateStock(int productId, int newQty) {
        String sql = "UPDATE products SET stock_qty = ? WHERE product_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newQty);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public int checkStock(int productId) {
        String sql = "SELECT stock_qty FROM products WHERE product_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("stock_qty");
            }
        } catch (SQLException e) {
            // ignore
        }
        return 0;
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        return new Product(
            rs.getInt("product_id"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getDouble("price"),
            rs.getInt("stock_qty")
        );
    }
}
```
