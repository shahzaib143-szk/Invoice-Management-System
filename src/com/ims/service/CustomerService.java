package com.ims.service;

import com.ims.dao.CustomerDAO;
import com.ims.model.Customer;
import java.util.List;

/**
 * Service Rule: validation + logic + calling DAO. Never SQL here.
 */
public class CustomerService {

    private final CustomerDAO customerDAO = new CustomerDAO();

    public boolean addCustomer(String name, String email, String phone, String address) {
        // Validation
        if (name == null || name.trim().isEmpty()) {
            System.err.println("[CustomerService] Name cannot be empty.");
            return false;
        }
        if (!email.contains("@")) {
            System.err.println("[CustomerService] Invalid email format.");
            return false;
        }
        // Duplicate check
        if (customerDAO.getCustomerByEmail(email) != null) {
            System.err.println("[CustomerService] Email already registered.");
            return false;
        }

        Customer c = new Customer(name.trim(), email.trim(), phone.trim(), address.trim());
        return customerDAO.insertCustomer(c);
    }

    public Customer findByEmail(String email) {
        return customerDAO.getCustomerByEmail(email);
    }

    public Customer findById(int id) {
        return customerDAO.getCustomerById(id);
    }

    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }

    public boolean updateCustomer(Customer c) {
        if (c.getCustomerId() <= 0) {
            System.err.println("[CustomerService] Invalid customer ID.");
            return false;
        }
        return customerDAO.updateCustomer(c);
    }

    public boolean deleteCustomer(int id) {
        return customerDAO.deleteCustomer(id);
    }
}
