package com.ims.ui;

import com.ims.dao.DBConnection;
import java.util.Scanner;

public class MainMenu {

    private final Scanner       sc           = new Scanner(System.in);
    private final CustomerMenu  customerMenu = new CustomerMenu();
    private final ProductMenu   productMenu  = new ProductMenu();
    private final InvoiceMenu   invoiceMenu  = new InvoiceMenu();

    public void run() {
        System.out.println("========================================");
        System.out.println("   Invoice Management System (IMS)    ");
        System.out.println("========================================");

        int choice;
        do {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Customer Management");
            System.out.println("2. Product Management");
            System.out.println("3. Invoice Management");
            System.out.println("0. Exit");
            System.out.print("Choice: ");

            choice = Integer.parseInt(sc.nextLine().trim());

            switch (choice) {
                case 1 -> customerMenu.show();
                case 2 -> productMenu.show();
                case 3 -> invoiceMenu.show();
                case 0 -> System.out.println("Goodbye!");
                default -> System.out.println("Invalid option.");
            }
        } while (choice != 0);

        DBConnection.closeConnection(); // Clean shutdown
    }
}

// ─────────────────────────────────────────────────────────────────────────────

class Main {
    public static void main(String[] args) {
        new MainMenu().run();
    }
}
