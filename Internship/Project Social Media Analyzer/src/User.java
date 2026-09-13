package com.socialnetwork.model;

/**
 * Represents a user in the social network.
 * Kept minimal — the friendship graph is managed in the service layer
 * using a Map<String, Set<String>> for efficient Set-algebra operations.
 */
public class User {

    private String username;

    public User() {}

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "User{username='" + username + "'}";
    }
}
