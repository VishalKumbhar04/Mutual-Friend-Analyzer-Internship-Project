package com.socialnetwork.controller;

import com.socialnetwork.model.User;
import com.socialnetwork.service.SocialNetworkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * REST Controller exposing all Social Network API endpoints.
 *
 * Base URL: http://localhost:8080
 *
 * CORS is enabled for all origins to allow the frontend (index.html)
 * to call these endpoints from any port during development.
 */
@RestController
@CrossOrigin(origins = "*")
public class SocialNetworkController {

    private final SocialNetworkService service;

    public SocialNetworkController(SocialNetworkService service) {
        this.service = service;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // USER ENDPOINTS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * POST /users
     * Body: { "username": "Alice" }
     * Creates a new user.
     */
    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody User user) {
        Map<String, Object> response = new LinkedHashMap<>();
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            response.put("success", false);
            response.put("message", "Username cannot be empty.");
            return ResponseEntity.badRequest().body(response);
        }
        boolean created = service.addUser(user.getUsername().trim());
        if (created) {
            response.put("success", true);
            response.put("message", "User '" + user.getUsername() + "' added successfully!");
        } else {
            response.put("success", false);
            response.put("message", "User '" + user.getUsername() + "' already exists.");
        }
        return ResponseEntity.ok(response);
    }

    /**
     * GET /users
     * Returns all users sorted alphabetically (backed by TreeSet in service).
     */
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<String> users = service.getAllUsers();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("users", users);
        response.put("count", users.size());
        return ResponseEntity.ok(response);
    }

    /**
     * GET /users/search?query=ali
     * Returns users matching the search query.
     */
    @GetMapping("/users/search")
    public ResponseEntity<Map<String, Object>> searchUsers(@RequestParam String query) {
        List<String> results = service.searchUsers(query);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("results", results);
        response.put("count", results.size());
        return ResponseEntity.ok(response);
    }

    /**
     * GET /users/top?n=5
     * Returns most connected users.
     */
    @GetMapping("/users/top")
    public ResponseEntity<Map<String, Object>> getMostConnected(
            @RequestParam(defaultValue = "5") int n) {
        List<Map<String, Object>> top = service.getMostConnectedUsers(n);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("topUsers", top);
        return ResponseEntity.ok(response);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FRIENDSHIP ENDPOINTS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * POST /friends
     * Body: { "userA": "Alice", "userB": "Bob" }
     * Creates bidirectional friendship.
     */
    @PostMapping("/friends")
    public ResponseEntity<Map<String, Object>> addFriend(@RequestBody Map<String, String> body) {
        String userA = body.get("userA");
        String userB = body.get("userB");
        Map<String, Object> response = new LinkedHashMap<>();

        if (userA == null || userB == null || userA.isBlank() || userB.isBlank()) {
            response.put("success", false);
            response.put("message", "Both userA and userB are required.");
            return ResponseEntity.badRequest().body(response);
        }
        if (userA.equals(userB)) {
            response.put("success", false);
            response.put("message", "A user cannot be friends with themselves.");
            return ResponseEntity.badRequest().body(response);
        }
        if (!service.userExists(userA)) {
            response.put("success", false);
            response.put("message", "User '" + userA + "' does not exist.");
            return ResponseEntity.badRequest().body(response);
        }
        if (!service.userExists(userB)) {
            response.put("success", false);
            response.put("message", "User '" + userB + "' does not exist.");
            return ResponseEntity.badRequest().body(response);
        }

        boolean added = service.addFriend(userA, userB);
        if (added) {
            response.put("success", true);
            response.put("message", userA + " and " + userB + " are now friends!");
        } else {
            response.put("success", false);
            response.put("message", userA + " and " + userB + " are already friends.");
        }
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /friends
     * Body: { "userA": "Alice", "userB": "Bob" }
     * Removes friendship.
     */
    @DeleteMapping("/friends")
    public ResponseEntity<Map<String, Object>> removeFriend(@RequestBody Map<String, String> body) {
        String userA = body.get("userA");
        String userB = body.get("userB");
        Map<String, Object> response = new LinkedHashMap<>();

        boolean removed = service.removeFriend(userA, userB);
        if (removed) {
            response.put("success", true);
            response.put("message", "Friendship between " + userA + " and " + userB + " removed.");
        } else {
            response.put("success", false);
            response.put("message", "Friendship not found between " + userA + " and " + userB + ".");
        }
        return ResponseEntity.ok(response);
    }

    /**
     * GET /friends/{user}
     * Returns sorted list of friends for a given user.
     */
    @GetMapping("/friends/{user}")
    public ResponseEntity<Map<String, Object>> getFriends(@PathVariable String user) {
        Map<String, Object> response = new LinkedHashMap<>();
        if (!service.userExists(user)) {
            response.put("success", false);
            response.put("message", "User '" + user + "' not found.");
            return ResponseEntity.badRequest().body(response);
        }
        List<String> friends = service.getFriends(user);
        response.put("user", user);
        response.put("friends", friends);
        response.put("count", friends.size());
        return ResponseEntity.ok(response);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SET ALGEBRA ENDPOINTS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * GET /mutual?user1=Alice&user2=Bob
     * Returns mutual friends (Set Intersection via retainAll()).
     * Output is sorted alphabetically (TreeSet used in service layer).
     */
    @GetMapping("/mutual")
    public ResponseEntity<Map<String, Object>> getMutualFriends(
            @RequestParam String user1,
            @RequestParam String user2) {

        Map<String, Object> response = new LinkedHashMap<>();

        if (!service.userExists(user1)) {
            response.put("success", false);
            response.put("message", "User '" + user1 + "' not found.");
            return ResponseEntity.badRequest().body(response);
        }
        if (!service.userExists(user2)) {
            response.put("success", false);
            response.put("message", "User '" + user2 + "' not found.");
            return ResponseEntity.badRequest().body(response);
        }

        List<String> mutual = service.getMutualFriends(user1, user2);
        response.put("user1", user1);
        response.put("user2", user2);
        response.put("mutualFriends", mutual);
        response.put("count", mutual.size());
        response.put("algorithm", "Set Intersection using retainAll() → wrapped in TreeSet for sorted output");
        return ResponseEntity.ok(response);
    }

    /**
     * GET /recommend/{user}
     * Returns friend recommendations ranked by mutual friend count.
     * Uses friends-of-friends logic with a scoring Map.
     */
    @GetMapping("/recommend/{user}")
    public ResponseEntity<Map<String, Object>> recommendFriends(@PathVariable String user) {
        Map<String, Object> response = new LinkedHashMap<>();
        if (!service.userExists(user)) {
            response.put("success", false);
            response.put("message", "User '" + user + "' not found.");
            return ResponseEntity.badRequest().body(response);
        }
        List<Map<String, Object>> recs = service.recommendFriends(user);
        response.put("user", user);
        response.put("recommendations", recs);
        response.put("count", recs.size());
        response.put("algorithm", "Friends-of-Friends scored by mutual connections, sorted by relevance");
        return ResponseEntity.ok(response);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DASHBOARD STATS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * GET /stats
     * Returns summary stats for the dashboard.
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("totalUsers", service.getUserCount());
        response.put("totalConnections", service.getTotalConnections());
        response.put("topUsers", service.getMostConnectedUsers(3));
        return ResponseEntity.ok(response);
    }
}
