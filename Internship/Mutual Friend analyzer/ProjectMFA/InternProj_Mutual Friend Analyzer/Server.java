import java.awt.Desktop;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class Server {

    private static final int PORT = 8080;

    // Shared data — same as Main.java's userFriendsMap
    // Loaded on startup from FileService.loadData()
    static Map<String, Set<String>> userFriendsMap = FileService.loadData();

    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = new ServerSocket(PORT);
        serverSocket.setReuseAddress(true);

        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║   Mutual Friend Analyzer — RUNNING       ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║   Open : http://localhost:8080           ║");
        System.out.println("║   Stop : Red Stop button in IntelliJ     ║");
        System.out.println("╚══════════════════════════════════════════╝");

        // Auto-open browser
        try {
            if (Desktop.isDesktopSupported())
                Desktop.getDesktop().browse(new URI("http://localhost:8080"));
        } catch (Exception e) {
            System.out.println("Visit manually: http://localhost:8080");
        }

        // Accept connections in a loop
        while (true) {
            Socket client = serverSocket.accept();
            new Thread(() -> handleRequest(client)).start();
        }
    }

    // ─────────────────────────────────────────────────────────
    //  HTTP REQUEST HANDLER
    // ─────────────────────────────────────────────────────────
    static void handleRequest(Socket client) {
        try (client) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
            OutputStream out = client.getOutputStream();

            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) return;

            String[] parts  = requestLine.split(" ");
            String method   = parts[0];
            String fullPath = parts.length > 1 ? parts[1] : "/";

            String path  = fullPath.contains("?") ? fullPath.substring(0, fullPath.indexOf('?')) : fullPath;
            String query = fullPath.contains("?") ? fullPath.substring(fullPath.indexOf('?') + 1) : "";

            // Read headers
            int contentLength = 0;
            String headerLine;
            while ((headerLine = in.readLine()) != null && !headerLine.isEmpty()) {
                if (headerLine.toLowerCase().startsWith("content-length:"))
                    contentLength = Integer.parseInt(headerLine.split(":")[1].trim());
            }

            // Read POST body
            String body = "";
            if (contentLength > 0) {
                char[] buf = new char[contentLength];
                in.read(buf, 0, contentLength);
                body = new String(buf);
            }

            // ── Route ─────────────────────────────────────────
            if (path.equals("/") || path.equals("/index.html")) {
                serveFile(out);
                return;
            }

            String response;
            switch (path) {
                case "/api/users":        response = handleGetUsers();         break;
                case "/api/user/add":     response = handleAddUser(body);      break;
                case "/api/user/remove":  response = handleRemoveUser(body);   break;
                case "/api/friend/add":   response = handleAddFriend(body);    break;
                case "/api/friend/remove":response = handleRemoveFriend(body); break;
                case "/api/mutual":       response = handleMutual(query);      break;
                case "/api/suggest":      response = handleSuggest(query);     break;
                case "/api/reset":        response = handleReset();            break;
                default:                  response = err("Route not found");   break;
            }

            sendJSON(out, response);

        } catch (Exception e) {
            System.out.println("Request error: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────
    //  SERVE index.html
    // ─────────────────────────────────────────────────────────
    static void serveFile(OutputStream out) throws IOException {
        try {
            byte[] html = Files.readAllBytes(Paths.get("index.html"));
            String header = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: text/html; charset=utf-8\r\n"
                    + "Content-Length: " + html.length + "\r\n"
                    + "Access-Control-Allow-Origin: *\r\n"
                    + "Connection: close\r\n\r\n";
            out.write(header.getBytes(StandardCharsets.UTF_8));
            out.write(html);
            out.flush();
        } catch (IOException e) {
            String msg = "<h2>index.html not found — place it in the same folder as Server.java and re-run.</h2>";
            byte[] b = msg.getBytes();
            String header = "HTTP/1.1 404 Not Found\r\nContent-Type: text/html\r\nContent-Length: "
                    + b.length + "\r\nConnection: close\r\n\r\n";
            out.write(header.getBytes(StandardCharsets.UTF_8));
            out.write(b);
            out.flush();
        }
    }

    // ─────────────────────────────────────────────────────────
    //  API HANDLERS
    // ─────────────────────────────────────────────────────────

    // GET /api/users
    // → reads Main.java userFriendsMap (loaded by FileService.loadData())
    static String handleGetUsers() {
        StringBuilder sb = new StringBuilder("{\"success\":true,\"users\":{");
        boolean first = true;
        for (Map.Entry<String, Set<String>> e : userFriendsMap.entrySet()) {
            if (!first) sb.append(",");
            first = false;
            sb.append("\"").append(e.getKey()).append("\":[")
                    .append(toJsonArray(e.getValue())).append("]");
        }
        return sb.append("}}").toString();
    }

    // POST /api/user/add
    // → Main.java case 1: userFriendsMap.putIfAbsent(user, new HashSet<>())
    // → FileService.java: saveData(userFriendsMap)
    static String handleAddUser(String body) {
        String username = extractJson(body, "username").trim().toLowerCase();
        if (username.isEmpty())                   return err("Username is empty");
        if (userFriendsMap.containsKey(username)) return err("User already exists");

        userFriendsMap.putIfAbsent(username, new HashSet<>());  // ← Main.java case 1
        FileService.saveData(userFriendsMap);                   // ← FileService.java
        return ok("User '" + username + "' added!");
    }

    // POST /api/user/remove
    // → Main.java: userFriendsMap.remove()
    // → FileService.java: saveData(userFriendsMap)
    static String handleRemoveUser(String body) {
        String username = extractJson(body, "username").trim().toLowerCase();
        if (!userFriendsMap.containsKey(username)) return err("User not found");

        userFriendsMap.remove(username);
        userFriendsMap.values().forEach(s -> s.remove(username));
        FileService.saveData(userFriendsMap);                   // ← FileService.java
        return ok("User '" + username + "' removed");
    }

    // POST /api/friend/add
    // → Main.java case 2: userFriendsMap.get(u1).add(f1)
    // → FileService.java: saveData(userFriendsMap)
    static String handleAddFriend(String body) {
        String user   = extractJson(body, "user").trim().toLowerCase();
        String friend = extractJson(body, "friend").trim().toLowerCase();
        boolean bidi  = Boolean.parseBoolean(extractJson(body, "bidirectional"));

        if (!userFriendsMap.containsKey(user))   return err("User not found");
        if (!userFriendsMap.containsKey(friend)) return err("Friend not found");
        if (user.equals(friend))                 return err("Cannot befriend self");

        userFriendsMap.get(user).add(friend);                   // ← Main.java case 2
        if (bidi) userFriendsMap.get(friend).add(user);
        FileService.saveData(userFriendsMap);                   // ← FileService.java
        return ok("Connected: " + user + (bidi ? " ↔ " : " → ") + friend);
    }

    // POST /api/friend/remove
    // → Main.java: userFriendsMap.get(user).remove(friend)
    // → FileService.java: saveData(userFriendsMap)
    static String handleRemoveFriend(String body) {
        String user   = extractJson(body, "user").trim().toLowerCase();
        String friend = extractJson(body, "friend").trim().toLowerCase();
        if (!userFriendsMap.containsKey(user)) return err("User not found");

        userFriendsMap.get(user).remove(friend);
        FileService.saveData(userFriendsMap);                   // ← FileService.java
        return ok("Removed " + friend + " from " + user);
    }

    // GET /api/mutual?user1=alice&user2=bob
    // → UserService.java: getMutualFriends(user1, user2, userFriendsMap)
    static String handleMutual(String query) {
        Map<String, String> q = parseQuery(query);
        String u1 = q.getOrDefault("user1", "").toLowerCase();
        String u2 = q.getOrDefault("user2", "").toLowerCase();

        Set<String> mutual = UserService.getMutualFriends(u1, u2, userFriendsMap); // ← UserService.java

        if (mutual == null) return err("One or both users not found");
        return "{\"success\":true,\"mutualFriends\":["
                + toJsonArray(mutual) + "],\"count\":" + mutual.size() + "}";
    }

    // GET /api/suggest?user1=alice&user2=bob
    // → UserService.java: suggestFriends(user1, user2, userFriendsMap)
    static String handleSuggest(String query) {
        Map<String, String> q = parseQuery(query);
        String u1 = q.getOrDefault("user1", "").toLowerCase();
        String u2 = q.getOrDefault("user2", "").toLowerCase();

        Set<String> suggestions = UserService.suggestFriends(u1, u2, userFriendsMap); // ← UserService.java

        if (suggestions == null) return err("One or both users not found");
        return "{\"success\":true,\"suggestions\":["
                + toJsonArray(suggestions) + "],\"count\":" + suggestions.size() + "}";
    }

    // POST /api/reset
    // → Main.java case 5: userFriendsMap.clear()
    // → FileService.java: saveData(userFriendsMap)
    static String handleReset() {
        userFriendsMap.clear();                                 // ← Main.java case 5
        FileService.saveData(userFriendsMap);                   // ← FileService.java
        return ok("All data cleared");
    }

    // ─────────────────────────────────────────────────────────
    //  UTILITIES
    // ─────────────────────────────────────────────────────────
    static void sendJSON(OutputStream out, String json) throws IOException {
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        String header = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: application/json; charset=utf-8\r\n"
                + "Content-Length: " + body.length + "\r\n"
                + "Access-Control-Allow-Origin: *\r\n"
                + "Connection: close\r\n\r\n";
        out.write(header.getBytes(StandardCharsets.UTF_8));
        out.write(body);
        out.flush();
    }

    static String extractJson(String json, String key) {
        String search = "\"" + key + "\"";
        int idx = json.indexOf(search);
        if (idx == -1) return "";
        int colon = json.indexOf(':', idx + search.length());
        if (colon == -1) return "";
        int start = colon + 1;
        while (start < json.length() && json.charAt(start) == ' ') start++;
        if (start >= json.length()) return "";
        if (json.charAt(start) == '"') {
            int end = json.indexOf('"', start + 1);
            return end == -1 ? "" : json.substring(start + 1, end);
        } else {
            int end = start;
            while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') end++;
            return json.substring(start, end).trim();
        }
    }

    static Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isEmpty()) return map;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                try { map.put(kv[0], URLDecoder.decode(kv[1], "UTF-8")); }
                catch (Exception e) { map.put(kv[0], kv[1]); }
            }
        }
        return map;
    }

    static String toJsonArray(Set<String> set) {
        if (set == null || set.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (String s : set) sb.append("\"").append(s).append("\",");
        return sb.substring(0, sb.length() - 1);
    }

    static String ok(String msg)  { return "{\"success\":true,\"message\":\"" + msg + "\"}"; }
    static String err(String msg) { return "{\"success\":false,\"message\":\"" + msg + "\"}"; }
}
