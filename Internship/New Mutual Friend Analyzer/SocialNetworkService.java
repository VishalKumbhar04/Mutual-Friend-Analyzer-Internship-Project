package com.socialnetwork.service;

import com.socialnetwork.model.User;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Core service implementing the Social Network using Java Set operations.
 *
 * DATA STRUCTURE:
 *   Map<String, Set<String>> network
 *   - Key: username (String)
 *   - Value: HashSet<String> of friend usernames
 *
 * Why HashSet?
 *   - O(1) average add/remove/contains operations
 *   - Automatically prevents duplicate friendships
 *
 * Why TreeSet (for output)?
 *   - Maintains natural (alphabetical) ordering
 *   - Makes results predictable and professional
 *
 * Set Algebra Used:
 *   - retainAll()  → Intersection → Mutual friends
 *   - addAll()     → Union        → Candidate pool for recommendations
 *   - removeAll()  → Difference   → Exclude already-known users
 */
@Service
public class SocialNetworkService {

    // Core adjacency structure: username → set of friends
    // HashSet chosen for O(1) lookups
    private final Map<String, Set<String>> network = new HashMap<>();

    // ─────────────────────────────────────────────────────────────────────────
    // USER MANAGEMENT
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Adds a new user to the network.
     *
     * @param username the unique username
     * @return true if user was new, false if already existed
     */
    public boolean addUser(String username) {
        if (username == null || username.isBlank()) return false;
        if (network.containsKey(username)) return false;
        // Each user starts with an empty HashSet of friends
        network.put(username, new HashSet<>());
        return true;
    }

    /**
     * Returns all users in the network, sorted alphabetically using TreeSet.
     */
    public List<String> getAllUsers() {
        // TreeSet gives us sorted output automatically
        return new ArrayList<>(new TreeSet<>(network.keySet()));
    }

    /**
     * Returns total number of registered users.
     */
    public int getUserCount() {
        return network.size();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FRIENDSHIP MANAGEMENT
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Creates a bidirectional friendship between userA and userB.
     * HashSet.add() silently ignores duplicate edges — no need for explicit checks.
     *
     * @return true if friendship was new, false if already existed or user missing
     */
    public boolean addFriend(String userA, String userB) {
        if (!network.containsKey(userA) || !network.containsKey(userB)) return false;
        if (userA.equals(userB)) return false;

        boolean added = network.get(userA).add(userB);
        network.get(userB).add(userA); // bidirectional
        return added;
    }

    /**
     * Removes a bidirectional friendship.
     *
     * @return true if friendship existed and was removed
     */
    public boolean removeFriend(String userA, String userB) {
        if (!network.containsKey(userA) || !network.containsKey(userB)) return false;
        boolean removed = network.get(userA).remove(userB);
        network.get(userB).remove(userA);
        return removed;
    }

    /**
     * Returns an alphabetically sorted list of a user's friends (TreeSet).
     */
    public List<String> getFriends(String username) {
        if (!network.containsKey(username)) return Collections.emptyList();
        // Wrap in TreeSet for sorted output
        return new ArrayList<>(new TreeSet<>(network.get(username)));
    }

    /**
     * Returns total number of unique friendships (edges).
     * Each friendship is stored twice (A→B and B→A), so we halve the count.
     */
    public int getTotalConnections() {
        int total = network.values().stream().mapToInt(Set::size).sum();
        return total / 2;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SET ALGEBRA: MUTUAL FRIENDS  (retainAll = Intersection)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Finds mutual friends between user1 and user2.
     *
     * ALGORITHM (Set Intersection):
     *   1. Get friendsOfUser1  (HashSet)
     *   2. Get friendsOfUser2  (HashSet)
     *   3. intersection = copy of friendsOfUser1
     *   4. intersection.retainAll(friendsOfUser2)
     *      → retainAll keeps ONLY elements present in BOTH sets
     *   5. Wrap in TreeSet for sorted output
     *
     * Time Complexity: O(n) where n = min(|friends1|, |friends2|)
     *
     * @return sorted list of mutual friends
     */
    public List<String> getMutualFriends(String user1, String user2) {
        if (!network.containsKey(user1) || !network.containsKey(user2))
            return Collections.emptyList();

        // Step 1: Copy friends of user1 into a new HashSet (don't mutate original!)
        Set<String> intersection = new HashSet<>(network.get(user1));

        // Step 2: retainAll performs SET INTERSECTION
        // After this, 'intersection' contains only users who are friends with BOTH
        intersection.retainAll(network.get(user2));

        // Step 3: TreeSet for alphabetical output
        return new ArrayList<>(new TreeSet<>(intersection));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // RECOMMENDATION ENGINE  (Friends-of-Friends logic)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Recommends friends for a user based on friends-of-friends logic.
     *
     * ALGORITHM:
     *   1. Get direct friends of 'user' → directFriends (Set)
     *   2. For each friend F in directFriends:
     *        For each friend-of-F (FOF):
     *          If FOF ≠ user AND FOF ∉ directFriends:
     *            score[FOF]++  (each mutual friend adds 1 to relevance score)
     *   3. Sort candidates by score descending
     *   4. Return ranked list with scores
     *
     * Why this works: A higher score means more mutual friends → stronger recommendation.
     *
     * @return list of maps [{username, mutualCount}] sorted by mutualCount desc
     */
    public List<Map<String, Object>> recommendFriends(String username) {
        if (!network.containsKey(username)) return Collections.emptyList();

        Set<String> directFriends = network.get(username);

        // Map to count how many mutual friends each candidate shares
        Map<String, Integer> scoreMap = new HashMap<>();

        for (String friend : directFriends) {
            // Get friends-of-friend
            Set<String> friendsOfFriend = network.get(friend);

            for (String candidate : friendsOfFriend) {
                // Skip self and already-connected users
                if (candidate.equals(username)) continue;
                if (directFriends.contains(candidate)) continue;

                // Increment mutual-friend score (this IS the mutual friend count)
                scoreMap.merge(candidate, 1, Integer::sum);
            }
        }

        // Convert to list of result maps and sort by score descending
        List<Map<String, Object>> recommendations = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : scoreMap.entrySet()) {
            Map<String, Object> rec = new LinkedHashMap<>();
            rec.put("username", entry.getKey());
            rec.put("mutualCount", entry.getValue());
            recommendations.add(rec);
        }

        // Sort by mutual friend count (descending), then alphabetically for ties
        recommendations.sort((a, b) -> {
            int cmp = Integer.compare((int) b.get("mutualCount"), (int) a.get("mutualCount"));
            if (cmp != 0) return cmp;
            return ((String) a.get("username")).compareTo((String) b.get("username"));
        });

        return recommendations;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SEARCH
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns users whose username contains the query (case-insensitive), sorted.
     */
    public List<String> searchUsers(String query) {
        String q = query.toLowerCase();
        Set<String> result = new TreeSet<>();
        for (String user : network.keySet()) {
            if (user.toLowerCase().contains(q)) result.add(user);
        }
        return new ArrayList<>(result);
    }

    /**
     * Returns the top N most connected users (sorted by friend count descending).
     */
    public List<Map<String, Object>> getMostConnectedUsers(int topN) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Map.Entry<String, Set<String>> entry : network.entrySet()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("username", entry.getKey());
            item.put("friendCount", entry.getValue().size());
            list.add(item);
        }
        list.sort((a, b) -> Integer.compare((int) b.get("friendCount"), (int) a.get("friendCount")));
        return list.subList(0, Math.min(topN, list.size()));
    }

    /**
     * Checks if a user exists.
     */
    public boolean userExists(String username) {
        return network.containsKey(username);
    }
}
