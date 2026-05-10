package com.ims.dao;

import com.ims.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    private final Connection conn =
            DBConnection.getConnection();

    public User login(String username,
                      String password) {

        String sql =
            "SELECT * FROM users " +
            "WHERE username = ? " +
            "AND password = ?";

        try (PreparedStatement ps =
                 conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs =
                    ps.executeQuery();

            if (rs.next()) {

                User user = new User();

                user.setUserId(
                    rs.getInt("user_id"));

                user.setUsername(
                    rs.getString("username"));

                user.setRole(
                    rs.getString("role"));

                user.setCustomerId(
                    rs.getInt("customer_id"));

                return user;
            }

        } catch (SQLException e) {

            System.err.println(
                "[UserDAO] Login failed: "
                + e.getMessage());
        }

        return null;
    }
}