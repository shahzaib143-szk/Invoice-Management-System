// User.java — in com.ims.model
package com.ims.model;

public class User {

    private int    userId;
    private String username;
    private String password;
    private String role;       // "ADMIN" or "USER"
    private int    customerId; // linked customer if USER

    public User() {}

    public User(int userId, String username,
                String role, int customerId) {
        this.userId     = userId;
        this.username   = username;
        this.role       = role;
        this.customerId = customerId;
    }

    public int    getUserId()     { return userId; }
    public String getUsername()   { return username; }
    public String getPassword()   { return password; }
    public String getRole()       { return role; }
    public int    getCustomerId() { return customerId; }

    public void setUserId(int id)          { this.userId = id; }
    public void setUsername(String u)      { this.username = u; }
    public void setPassword(String p)      { this.password = p; }
    public void setRole(String r)          { this.role = r; }
    public void setCustomerId(int cid)     { this.customerId = cid; }

    public boolean isAdmin() {
        return "ADMIN".equals(this.role);
    }
}