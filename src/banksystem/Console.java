/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package banksystem;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

/**
 *
 * @author nazmul
 */
class Console {
    Bank bank;
    ExecutorService executor;
    User loggedInUser;

    public Console(Bank bank) {
        this.bank = bank;
        this.executor = Executors.newFixedThreadPool(5);
        this.loggedInUser = null;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            switch (option) {
                case 1:
                    registerUser(scanner);
                    break;
                case 2:
                    loginUser(scanner);
                    break;
                case 3:
                    System.exit(0);
                default:
                    System.out.println("Invalid option. Please choose again.");
            }
        }
    }

    private void registerUser(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.next();
        System.out.print("Enter password: ");
        String password = scanner.next();
        System.out.print("Enter initial balance: ");
        double balance = scanner.nextDouble();
        User user = new User(username, password, balance);
        try {
            bank.addUser(user);
            System.out.println("User registered successfully.");
        } catch (SQLException e) {
            System.out.println("Error registering user: " + e.getMessage());
        }
    }

    private void loginUser(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.next();
        System.out.print("Enter password: ");
        String password = scanner.next();
        User user = getUser(username, password);
        if (user != null) {
            user.login();
            loggedInUser = user;
            System.out.println("Login successful. Your balance is: " + user.balance);
            while (loggedInUser.isLoggedIn()) {
                System.out.println("1. Deposit");
                System.out.println("2. Withdraw");
                System.out.println("3. Check balance");
                System.out.println("4. Transfer money");
                System.out.println("5. Logout");
                System.out.print("Choose an option: ");
                int option = scanner.nextInt();
                switch (option) {
                    case 1:
                        deposit(loggedInUser, scanner);
                        break;
                    case 2:
                        withdraw(loggedInUser, scanner);
                        break;
                    case 3:
                        System.out.println("Your balance is: " + loggedInUser.balance);
                        break;
                    case 4:
                        transferMoney(scanner);
                        break;
                    case 5:
                        loggedInUser.logout();
                        loggedInUser = null;
                        System.out.println("Logged out successfully.");
                        break;
                    default:
                        System.out.println("Invalid option. Please choose again.");
                }
            }
        } else {
            System.out.println("Invalid username or password.");
        }
    }

    private void transferMoney(Scanner scanner) {
        System.out.print("Enter receiver username: ");
        String receiver = scanner.next();
        System.out.print("Enter amount to transfer: ");
        double amount = scanner.nextDouble();
        try {
            bank.transferMoney(loggedInUser.username, receiver, amount);
        } catch (SQLException e) {
            System.out.println("Error transferring money: " + e.getMessage());
        }
    }

    private User getUser(String username, String password) {
        try {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement statement = bank.connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return new User(result.getString("username"), result.getString("password"), result.getDouble("balance"));
            }
        } catch (SQLException e) {
            System.out.println("Error getting user: " + e.getMessage());
        }
        return null;
    }

    private void deposit(User user, Scanner scanner) {
        System.out.print("Enter amount to deposit: ");
        double amount = scanner.nextDouble();
        executor.submit(() -> {
            try {
                String query = "UPDATE users SET balance = balance + ? WHERE username = ?";
                PreparedStatement statement = bank.connection.prepareStatement(query);
                statement.setDouble(1, amount);
                statement.setString(2, user.username);
                statement.executeUpdate();
                user.balance += amount; 
                System.out.println("Deposit successful. Your new balance is: " + user.balance);
            } catch (SQLException e) {
                System.out.println("Error depositing: " + e.getMessage());
            }
        });
    }

    private void withdraw(User user, Scanner scanner) {
        System.out.print("Enter amount to withdraw: ");
        double amount = scanner.nextDouble();
        if (amount <= user.balance) {
            executor.submit(() -> {
                try {
                    String query = "UPDATE users SET balance = balance - ? WHERE username = ?";
                    PreparedStatement statement = bank.connection.prepareStatement(query);
                    statement.setDouble(1, amount);
                    statement.setString(2, user.username);
                    statement.executeUpdate();
                    user.balance -= amount; 
                    System.out.println("Withdrawal successful. Your new balance is: " + user.balance);
                } catch (SQLException e) {
                    System.out.println("Error withdrawing: " + e.getMessage());
                }
            });
        } else {
            System.out.println("Insufficient balance.");
        }
    }
}
