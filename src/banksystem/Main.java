/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package banksystem;

import java.sql.SQLException;

/**
 *
 * @author nazmul
 */
public class Main {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3307/nazbank";
        String username = "root";
        String password = "";
        try {
            Bank bank = new Bank(url, username, password);
            Console console = new Console(bank);
            console.start();
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
        }
    }
}