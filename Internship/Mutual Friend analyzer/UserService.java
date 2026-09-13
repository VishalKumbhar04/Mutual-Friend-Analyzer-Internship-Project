import java.util.*;

/**
 * UserService.java
 * — getMutualFriends() : returns intersection of two friend sets (sorted TreeSet)
 * — suggestFriends()   : returns union minus mutual minus already-friends
 */
public class UserService {

    // Returns sorted mutual friends, or null if a user doesn't exist
    public static Set<String> getMutualFriends(String user1, String user2,
                                               Map<String, Set<String>> map) {
        if (!map.containsKey(user1) || !map.containsKey(user2)) return null;

        Set<String> set1 = map.get(user1);
        Set<String> set2 = map.get(user2);

        Set<String> mutual = new TreeSet<>(set1); // TreeSet keeps sorted order
        mutual.retainAll(set2);                   // intersection
        return mutual;
    }

    // Returns suggested friends, or null if a user doesn't exist
    public static Set<String> suggestFriends(String user1, String user2,
                                             Map<String, Set<String>> map) {
        if (!map.containsKey(user1) || !map.containsKey(user2)) return null;

        Set<String> set1 = new HashSet<>(map.get(user1));
        Set<String> set2 = new HashSet<>(map.get(user2));

        set1.addAll(set2);                                          // union
        Set<String> mutual = getMutualFriends(user1, user2, map);
        set1.removeAll(mutual);                                     // remove mutual
        set1.removeAll(map.get(user1));                             // remove already friends

        return new TreeSet<>(set1); // sorted
    }
}
