package com.example.newschatbot;



public class HelperClass {

    String name1, email1, username, password1;

    public String getName() {
        return name1;
    }

    public void setName(String name) {
        this.name1 = name;
    }

    public String getEmail() {
        return email1;
    }

    public void setEmail(String email) {
        this.email1 = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password1;
    }

    public void setPassword(String password) {
        this.password1 = password;
    }

    public HelperClass(String name, String email, String username, String password) {
        this.name1 = name;
        this.email1 = email;
        this.username = username;
        this.password1 = password;
    }


}
