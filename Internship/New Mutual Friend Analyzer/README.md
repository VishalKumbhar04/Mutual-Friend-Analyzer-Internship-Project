# NetGraph — Social Network Analyzer
### Using Set Algebra (Java Spring Boot + Vanilla JS)

---

## Project Overview

A full-stack social network application that demonstrates real-world usage of Java's **Collection Framework**, specifically `HashSet`, `TreeSet`, and Set operations like `retainAll()`. The backend is built with **Spring Boot**, and the frontend is a responsive single-page application using HTML, CSS, and JavaScript.

---

## Project Structure

```
social-network/
│
├── backend/
│   ├── pom.xml
│   └── src/main/java/com/socialnetwork/
│       ├── SocialNetworkApplication.java     ← Entry point + sample data loader
│       ├── model/
│       │   └── User.java                     ← Simple User POJO
│       ├── service/
│       │   └── SocialNetworkService.java     ← All Set-algebra logic lives here
│       └── controller/
│           └── SocialNetworkController.java  ← REST API endpoints
│
└── frontend/
    ├── index.html    ← Single-page layout with sidebar navigation
    ├── style.css     ← Complete design system (Syne + DM Sans, blue/teal theme)
    └── script.js     ← Fetch API calls + dynamic UI rendering
```

---

## Tech Stack

| Layer      | Technology                           |
|------------|--------------------------------------|
| Backend    | Java 17, Spring Boot 3.2, Maven      |
| Data Store | In-memory `Map<String, Set<String>>` |
| Frontend   | HTML5, CSS3, Vanilla JavaScript      |
| Fonts      | Syne (display), DM Sans (body)       |

---

## How to Run

### 1. Start the Backend

```bash
cd backend
mvn spring-boot:run
```

The server starts at `http://localhost:8080`  
Sample data (6 users + 9 friendships) is loaded automatically.

### 2. Open the Frontend

Simply open `frontend/index.html` in your browser.  
The frontend calls `http://localhost:8080` via `fetch()`.

---

## REST API Endpoints

| Method | Endpoint              | Description                                  |
|--------|-----------------------|----------------------------------------------|
| POST   | `/users`              | Register a new user                          |
| GET    | `/users`              | List all users (sorted via TreeSet)          |
| GET    | `/users/search?query=` | Search users by name                        |
| GET    | `/users/top?n=5`      | Top N most connected users                  |
| POST   | `/friends`            | Add friendship between two users             |
| DELETE | `/friends`            | Remove a friendship                          |
| GET    | `/friends/{user}`     | Get a user's friend list (sorted)            |
| GET    | `/mutual?user1=&user2=` | Find mutual friends (Set intersection)    |
| GET    | `/recommend/{user}`   | Get ranked friend recommendations            |
| GET    | `/stats`              | Dashboard stats (users, connections, top 3) |

---

## Core Data Structure

```java
Map<String, Set<String>> network = new HashMap<>();
// Example state:
// "Alice" → HashSet{"Bob", "Charlie", "Diana"}
// "Bob"   → HashSet{"Alice", "Charlie", "Eve"}
```

**Why `Map<String, Set<String>>`?**
- `HashMap` — O(1) average lookup to find any user's friend set
- `HashSet` — O(1) average add/remove/contains within friend sets
- Duplicate friendships are **automatically prevented** by Set semantics

---

## HashSet — For Storing Friends

```java
network.put("Alice", new HashSet<>());
network.get("Alice").add("Bob");    // O(1) — no duplicates possible
network.get("Alice").add("Charlie");
```

**Why HashSet?**
- Backed by a hash table → average O(1) for add, remove, contains
- No duplicate entries — perfect for friendship graphs
- Memory-efficient for sparse graphs

---

## TreeSet — For Sorted Output

```java
// Wrap any HashSet result in TreeSet for sorted output
List<String> sortedFriends = new ArrayList<>(new TreeSet<>(network.get(user)));
```

**Why TreeSet for output?**
- Backed by a Red-Black Tree → always maintains sorted (natural) order
- Guarantees alphabetical results — professional, predictable output
- O(n log n) cost is acceptable for output rendering

---

## `retainAll()` — Mutual Friends (Set Intersection)

```java
public List<String> getMutualFriends(String user1, String user2) {
    // Step 1: Copy friends of user1 (never mutate the original Set!)
    Set<String> intersection = new HashSet<>(network.get(user1));

    // Step 2: retainAll keeps ONLY elements present in BOTH sets
    //         This is Set Intersection: A ∩ B
    intersection.retainAll(network.get(user2));

    // Step 3: Wrap in TreeSet for alphabetical output
    return new ArrayList<>(new TreeSet<>(intersection));
}
```

**How `retainAll()` works:**  
Iterates through `intersection`. For each element, it checks if it exists in `network.get(user2)`. If not → removes it.  
Result: only elements present in **both** sets remain.

**Complexity:** O(n) where n = size of the smaller set

**Example:**
```
Alice's friends: {Bob, Charlie, Diana}
Bob's friends:   {Alice, Charlie, Eve}

intersection = copy of {Bob, Charlie, Diana}
retainAll({Alice, Charlie, Eve})
→ "Bob"    not in Bob's friends? No → remove
→ "Charlie" in Bob's friends? Yes → keep
→ "Diana"   not in Bob's friends? No → remove

Result: {Charlie}
Wrapped in TreeSet: [Charlie]  ✓ alphabetically sorted
```

---

## Friend Recommendation Logic

```java
public List<Map<String, Object>> recommendFriends(String username) {
    Set<String> directFriends = network.get(username);
    Map<String, Integer> scoreMap = new HashMap<>();

    for (String friend : directFriends) {
        for (String candidate : network.get(friend)) {
            if (candidate.equals(username)) continue;       // skip self
            if (directFriends.contains(candidate)) continue; // skip already friends

            // Each shared friend adds 1 to the candidate's relevance score
            scoreMap.merge(candidate, 1, Integer::sum);
        }
    }

    // Sort by score descending (more mutual friends = stronger recommendation)
    ...
}
```

**Algorithm: Friends-of-Friends (FoF)**

1. Start from user `U`'s direct friends
2. Explore each friend's friend list (2-hop neighbors)
3. Exclude `U` and anyone already connected to `U`
4. Count how many of `U`'s friends are also connected to each candidate
5. This count = the **mutual friend score**
6. Sort candidates by score descending

**Example:**
```
Network:
Alice → {Bob, Charlie, Diana}
Bob   → {Alice, Charlie, Eve}
Charlie → {Alice, Bob, Diana, Frank}

Recommendations for Alice:
- Eve:   Bob is mutual → score = 1
- Frank: Charlie is mutual → score = 1
Both score 1 → sorted alphabetically: [Eve, Frank]
```

---

## Frontend Features

| Feature              | Implementation                                     |
|----------------------|----------------------------------------------------|
| Navigation           | Sidebar with JS-driven single-page switching       |
| Add User             | POST `/users` + toast notification                 |
| Add Friend           | POST `/friends` with dynamic dropdowns             |
| Mutual Friends       | GET `/mutual` + sorted list display                |
| Recommendations      | GET `/recommend/{user}` + score bars               |
| Explore Network      | GET `/friends/{user}` + friend list                |
| Global Search        | GET `/users/search` with debounced input           |
| Dashboard Stats      | GET `/stats` — users, connections, top connectors  |

---

## Sample Data (Loaded on Startup)

```
Users: Alice, Bob, Charlie, Diana, Eve, Frank

Friendships:
Alice — Bob, Charlie, Diana
Bob   — Alice, Charlie, Eve
Charlie — Alice, Bob, Diana, Frank
Diana — Alice, Charlie, Eve
Eve — Bob, Diana, Frank
Frank — Charlie, Eve
```

**Good test cases:**
- Mutual friends (Alice, Bob) → `Charlie`
- Mutual friends (Alice, Eve) → `Bob, Diana`
- Recommendations for Alice → `Eve (1), Frank (1)`
- Recommendations for Bob → `Diana (2), Frank (1)`

---

## Interview Talking Points

1. **Why HashSet over ArrayList for friends?**  
   - HashSet: O(1) contains/add vs ArrayList: O(n) → critical for `retainAll()` performance

2. **Why copy the Set before `retainAll()`?**  
   - `retainAll()` mutates the collection in-place. Always copy first to preserve original data.

3. **Why TreeSet for output?**  
   - TreeSet is always sorted; wrapping any Set in `new TreeSet<>(set)` gives O(n log n) sorted copy.

4. **Time complexity of mutual friends?**  
   - O(min(|A|, |B|)) for the intersection — HashSet's O(1) contains makes this linear.

5. **Why Map<String, Integer> for recommendation scores?**  
   - `merge(key, 1, Integer::sum)` is a clean atomic increment pattern — idiomatic Java.
