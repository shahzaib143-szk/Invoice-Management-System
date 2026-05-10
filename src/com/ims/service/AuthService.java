
package com.ims.service;
import com.ims.dao.UserDAO;
import com.ims.model.User;

public class AuthService {

    private static final UserDAO userDAO = new UserDAO();
    private static User currentUser;

    public static User login(String username, String password) {

        if (username == null || username.trim().isEmpty()) {
            System.out.println("Username required.");
            return null;
        }

        if (password == null || password.trim().isEmpty()) {
            System.out.println("Password required.");
            return null;
        }

        User user = userDAO.login(
                username.trim(),
                password.trim()
        );

        if (user != null) {
            currentUser = user;

            System.out.println(
                "Login success: " +
                user.getUsername() +
                " | Role: " +
                user.getRole()
            );
        }

        return user;
    }

    public static void logout() {
        currentUser = null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static boolean isAdmin() {
        return currentUser != null &&
               "ADMIN".equals(currentUser.getRole());
    }

    public static boolean isUser() {
        return currentUser != null &&
               "USER".equals(currentUser.getRole());
    }

    public static String getDashboardPath() {

        if (currentUser == null) {
            return "/com/ims/gui/view/login.fxml";
        }

        if ("ADMIN".equals(currentUser.getRole())) {
            return "/com/ims/gui/view/admin-dashboard.fxml";
        }

        return "/com/ims/gui/view/user-dashboard.fxml";
    }
}