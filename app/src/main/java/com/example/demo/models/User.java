package com.example.demo.models;

import java.util.Date;

public class User {
    String userID, name, email, password, age;
    int weight;
//    Date dateOfBirth;

//    public User(String userID, String name, String email, String password, int weight, Date dateOfBirth) {
//        this.userID = userID;
//        this.name = name;
//        this.email = email;
//        this.password = password;
//        this.weight = weight;
//        this.dateOfBirth = dateOfBirth;
//    }
public User(String userID, String name, String email, String password, int weight) {
    this.userID = userID;
    this.name = name;
    this.email = email;
    this.password = password;
    this.weight = weight;
}
    public String getName() {
        return name;
    }
    public String getAge() {return age;}

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
