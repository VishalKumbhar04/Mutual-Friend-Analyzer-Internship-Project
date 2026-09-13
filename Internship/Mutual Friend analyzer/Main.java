import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.awt.Desktop;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * ─────────────────────────────────────────────────
 *  Main.java
 *  → Run this file in IntelliJ to launch the GUI
 *  → Starts a local server on http://localhost:8080
 *  → Opens the browser automatically
 *
 *  Uses:
 *    UserService.java  — getMutualFriends(), suggestFriends()
 *    FileService.java  — saveData(), loadData()
 * ─────────────────────────────────────────────────
 */
public class Main {

    // Shared in-memory store — loaded from data.ser via FileService on startup
    static Map<String, Set<String>> userFriendsMap = FileService.loadData();

    public static void main(String[] args) throws Exception {

        // ── Start HTTP server ──────────────────────────────────────
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/",                  Main::serveUI);
        server.createContext("/api/users",         Main::getUsers);
        server.createContext("/api/user/add",      Main::addUser);
        server.createContext("/api/user/remove",   Main::removeUser);
        server.createContext("/api/friend/add",    Main::addFriend);
        server.createContext("/api/friend/remove", Main::removeFriend);
        server.createContext("/api/mutual",        Main::getMutual);
        server.createContext("/api/suggest",       Main::getSuggest);
        server.createContext("/api/reset",         Main::reset);

        server.setExecutor(null);
        server.start();

        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║   Mutual Friend Analyzer  — RUNNING      ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║   http://localhost:8080                  ║");
        System.out.println("║   Opening browser...                     ║");
        System.out.println("║   Stop: click the red Stop button        ║");
        System.out.println("╚══════════════════════════════════════════╝");

        // ── Open browser automatically ─────────────────────────────
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI("http://localhost:8080"));
            } else {
                // Linux fallback
                Runtime.getRuntime().exec("xdg-open http://localhost:8080");
            }
        } catch (Exception e) {
            System.out.println("   → Could not open browser. Visit http://localhost:8080 manually.");
        }
    }

    // ── Serve the HTML UI ──────────────────────────────────────────
    static void serveUI(HttpExchange ex) throws IOException {
        String html = UITemplate.getHTML();
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
        ex.sendResponseHeaders(200, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.close();
    }

    // ── GET /api/users ─────────────────────────────────────────────
    static void getUsers(HttpExchange ex) throws IOException {
        StringBuilder sb = new StringBuilder("{\"success\":true,\"users\":{");
        boolean first = true;
        for (Map.Entry<String, Set<String>> e : userFriendsMap.entrySet()) {
            if (!first) sb.append(",");
            first = false;
            sb.append("\"").append(e.getKey()).append("\":[");
            sb.append(toJsonArray(e.getValue()));
            sb.append("]");
        }
        sb.append("}}");
        send(ex, 200, sb.toString());
    }

    // ── POST /api/user/add ─────────────────────────────────────────
    // Mirrors: userFriendsMap.putIfAbsent(user, new HashSet<>());
    static void addUser(HttpExchange ex) throws IOException {
        Map<String, String> body = parseBody(ex);
        String username = body.getOrDefault("username", "").trim().toLowerCase();
        if (username.isEmpty())                        { send(ex,400,err("Username is empty")); return; }
        if (userFriendsMap.containsKey(username))      { send(ex,400,err("User already exists")); return; }
        userFriendsMap.put(username, new HashSet<>());
        FileService.saveData(userFriendsMap);           // FileService.saveData()
        send(ex, 200, ok("User '" + username + "' added!"));
    }

    // ── POST /api/user/remove ──────────────────────────────────────
    static void removeUser(HttpExchange ex) throws IOException {
        Map<String, String> body = parseBody(ex);
        String username = body.getOrDefault("username", "").trim().toLowerCase();
        if (!userFriendsMap.containsKey(username)) { send(ex,400,err("User not found")); return; }
        userFriendsMap.remove(username);
        userFriendsMap.values().forEach(s -> s.remove(username));
        FileService.saveData(userFriendsMap);
        send(ex, 200, ok("User '" + username + "' removed"));
    }

    // ── POST /api/friend/add ───────────────────────────────────────
    // Mirrors: userFriendsMap.get(u1).add(f1);
    static void addFriend(HttpExchange ex) throws IOException {
        Map<String, String> body = parseBody(ex);
        String user   = body.getOrDefault("user",   "").trim().toLowerCase();
        String friend = body.getOrDefault("friend", "").trim().toLowerCase();
        boolean bidi  = Boolean.parseBoolean(body.getOrDefault("bidirectional", "true"));
        if (!userFriendsMap.containsKey(user))   { send(ex,400,err("User not found"));   return; }
        if (!userFriendsMap.containsKey(friend)) { send(ex,400,err("Friend not found")); return; }
        if (user.equals(friend))                 { send(ex,400,err("Cannot befriend self")); return; }
        userFriendsMap.get(user).add(friend);
        if (bidi) userFriendsMap.get(friend).add(user);
        FileService.saveData(userFriendsMap);
        send(ex, 200, ok("Connected: " + user + (bidi?" ↔ ":" → ") + friend));
    }

    // ── POST /api/friend/remove ────────────────────────────────────
    static void removeFriend(HttpExchange ex) throws IOException {
        Map<String, String> body = parseBody(ex);
        String user   = body.getOrDefault("user",   "").trim().toLowerCase();
        String friend = body.getOrDefault("friend", "").trim().toLowerCase();
        if (!userFriendsMap.containsKey(user)) { send(ex,400,err("User not found")); return; }
        userFriendsMap.get(user).remove(friend);
        FileService.saveData(userFriendsMap);
        send(ex, 200, ok("Removed " + friend + " from " + user));
    }

    // ── GET /api/mutual?user1=&user2= ──────────────────────────────
    // Calls UserService.getMutualFriends()
    static void getMutual(HttpExchange ex) throws IOException {
        Map<String, String> q = parseQuery(ex.getRequestURI().getQuery());
        String u1 = q.getOrDefault("user1","").toLowerCase();
        String u2 = q.getOrDefault("user2","").toLowerCase();
        Set<String> mutual = UserService.getMutualFriends(u1, u2, userFriendsMap);
        if (mutual == null) { send(ex,400,err("One or both users not found")); return; }
        send(ex, 200, "{\"success\":true,\"mutualFriends\":[" + toJsonArray(mutual) + "],\"count\":" + mutual.size() + "}");
    }

    // ── GET /api/suggest?user1=&user2= ─────────────────────────────
    // Calls UserService.suggestFriends()
    static void getSuggest(HttpExchange ex) throws IOException {
        Map<String, String> q = parseQuery(ex.getRequestURI().getQuery());
        String u1 = q.getOrDefault("user1","").toLowerCase();
        String u2 = q.getOrDefault("user2","").toLowerCase();
        Set<String> suggestions = UserService.suggestFriends(u1, u2, userFriendsMap);
        if (suggestions == null) { send(ex,400,err("One or both users not found")); return; }
        send(ex, 200, "{\"success\":true,\"suggestions\":[" + toJsonArray(suggestions) + "],\"count\":" + suggestions.size() + "}");
    }

    // ── POST /api/reset ────────────────────────────────────────────
    static void reset(HttpExchange ex) throws IOException {
        userFriendsMap.clear();
        FileService.saveData(userFriendsMap);
        send(ex, 200, ok("All data cleared"));
    }

    // ── Helpers ────────────────────────────────────────────────────
    static String toJsonArray(Set<String> set) {
        if (set == null || set.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (String s : set) sb.append("\"").append(s).append("\",");
        return sb.substring(0, sb.length() - 1);
    }

    static Map<String, String> parseBody(HttpExchange ex) throws IOException {
        String raw = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> map = new HashMap<>();
        raw = raw.replaceAll("[{}\"]", "");
        for (String pair : raw.split(",")) {
            String[] kv = pair.split(":", 2);
            if (kv.length == 2) map.put(kv[0].trim(), kv[1].trim());
        }
        return map;
    }

    static Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null) return map;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) map.put(kv[0], kv[1]);
        }
        return map;
    }

    static void send(HttpExchange ex, int code, String json) throws IOException {
        ex.getResponseHeaders().set("Content-Type", "application/json");
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        byte[] b = json.getBytes(StandardCharsets.UTF_8);
        ex.sendResponseHeaders(code, b.length);
        ex.getResponseBody().write(b);
        ex.close();
    }

    static String ok(String msg)  { return "{\"success\":true,\"message\":\"" + msg + "\"}"; }
    static String err(String msg) { return "{\"success\":false,\"message\":\"" + msg + "\"}"; }
}
