/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package banksystem;

/**
 *
 * @author nazmul
 */
class User {
    String username;
    String password;
    double balance;
    boolean loggedIn;

    public User(String username, String password, double balance) {
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.loggedIn = false; 
    }

    boolean isLoggedIn() {
        return this.loggedIn;
    }

    void login() {
        this.loggedIn = true;
    }

    void logout() {
        this.loggedIn = false;
    }
}

