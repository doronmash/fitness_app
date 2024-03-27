package com.example.demo.models;

import java.util.Date;

public class User {
    String userID, name, email, password, age;
    int weight;
    Date dateOfBirth;
    public User() {
    }
    public User(String userID, String name, String email, String password, int weight, Date dateOfBirth) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.password = password;
        this.weight = weight;
        this.dateOfBirth = dateOfBirth;
    }

    public String getName() {
        return name;
    }
    public String getAge() {return age;}

    public String getUserID() {
        return userID;
    }

    public int getWeight() {
        return weight;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

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
