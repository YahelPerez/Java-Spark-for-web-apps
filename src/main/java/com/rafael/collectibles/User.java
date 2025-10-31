package com.rafael.collectibles;

/**
 * Simple POJO for a User.
 */
public class User {
    private String id;
    private String name;
    private String email;

    // No-arg constructor needed for Gson deserialization
    public User() { }

    public User(String name, String email) { this.name = name; this.email = email; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
