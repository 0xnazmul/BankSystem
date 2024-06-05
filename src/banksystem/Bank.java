/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package banksystem;

import com.mysql.jdbc.Connection;
import java.sql.*;
import java.util.*;

/**
 *
 * @author nazmul
 */
class Bank {
    Connection connection;
    Map<String, User> users;

    public Bank(String url, String username, String password) throws SQLException {
        this.users = new HashMap<>();
        this.connection = (Connection) DriverManager.getConnection(url, username, password);
        createTable();
    }

    public void addUser(User user) throws SQLException {
        // Check if the username already exists
        if (getUser(user.username) != null) {
            throw new SQLException("Username already exists.");
        }
        String query = "INSERT INTO users (username, password, balance) VALUES (?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, user.username);
        statement.setString(2, user.password);
        statement.setDouble(3, user.balance);
        statement.executeUpdate();
        users.put(user.username, user);
    }

    public User getUser(String username) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);
        ResultSet result = statement.executeQuery();
        if (result.next()) {
            User user = new User(result.getString("username"), result.getString("password"), result.getDouble("balance"));
            users.put(username, user); 
            return user;
        }
        return null;
    }

    public void transferMoney(String sender, String receiver, double amount) throws SQLException {
        User senderUser = getUser(sender);
        User receiverUser = getUser(receiver);
        if (senderUser != null && receiverUser != null) {
            if (senderUser.isLoggedIn()) {
                if (senderUser.balance >= amount) {
                    senderUser.balance -= amount;
                    receiverUser.balance += amount;
                    String query = "UPDATE users SET balance = ? WHERE username = ?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setDouble(1, senderUser.balance);
                    statement.setString(2, senderUser.username);
                    statement.executeUpdate();
                    users.put(senderUser.username, senderUser); 
                    query = "UPDATE users SET balance = ? WHERE username = ?";
                    statement = connection.prepareStatement(query);
                    statement.setDouble(1, receiverUser.balance);
                    statement.setString(2, receiverUser.username);
                    statement.executeUpdate();
                    users.put(receiverUser.username, receiverUser); 
                    System.out.println("Transfer successful.");
                } else {
                    System.out.println("Insufficient balance.");
                }
            } else {
                System.out.println("You must be logged in to transfer money.");
            }
        } else {
            System.out.println("Invalid user.");
        }
    }

    private void createTable() throws SQLException {
        String query = "CREATE TABLE IF NOT EXISTS users (username VARCHAR(255), password VARCHAR(255), balance DOUBLE)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.executeUpdate();
    }
}
