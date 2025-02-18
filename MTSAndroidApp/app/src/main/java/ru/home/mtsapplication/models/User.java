package ru.home.mtsapplication.models;

public class User {

    String name;
    String username;
    String password;
    int age;

    public User(String name, String username, String password, int age) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.age = age;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.age = -1;
        this.name = "";
    }


}
