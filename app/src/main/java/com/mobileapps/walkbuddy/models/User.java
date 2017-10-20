package com.mobileapps.walkbuddy.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by kurti on 10/5/2017.
 */
@IgnoreExtraProperties
public class User {
    private static final String DEFAULT_NAME = "name";
    private static final String DEFAULT_EMAIL = "email";

    private String name;
    private String email;

    public User() {
        // Default constructor
        this(DEFAULT_NAME, DEFAULT_EMAIL);
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
