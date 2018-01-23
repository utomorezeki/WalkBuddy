package com.mobileapps.walkbuddy.models;

/**
 * Created by kurti on 10/5/2017.
 */
public class User {
    private String name;
    private String email;

    public User() {
        // Empty constructor needed for firebase
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return this.name;
    }
    public String getEmail() {
        return this.email;
    }
}
