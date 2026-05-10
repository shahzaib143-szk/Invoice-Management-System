package com.ims.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

 */
public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/ims_db";

    private static final String USER = "javauser";
    private static final String PASSWORD = "1234";
    private static Connection instance = null;

    private DBConnection() {}

    public static Connection getConnection() {
        try {
            if (instance == null || instance.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                instance = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[DB] Connection established.");
            }

            return instance;

        } catch (ClassNotFoundException e) {
            throw new RuntimeException("[DB] MySQL Driver not found", e);

        } catch (SQLException e) {
            throw new RuntimeException("[DB] Connection failed: " + e.getMessage(), e);
        }
    }

    public static void closeConnection() {
        try {
            if (instance != null && !instance.isClosed()) {
                instance.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("[DB] Error closing connection", e);
        }
    }
}