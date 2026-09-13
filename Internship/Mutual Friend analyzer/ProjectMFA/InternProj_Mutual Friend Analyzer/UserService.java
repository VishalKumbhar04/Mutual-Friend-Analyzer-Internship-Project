import java.util.*;

public class UserService {

    // 🔹 Mutual Friends
    public static Set<String> getMutualFriends(String user1, String user2,
                                               Map<String, Set<String>> map) {

        if (!map.containsKey(user1) || !map.containsKey(user2)) {
            return null;
        }

        Set<String> set1 = map.get(user1);
        Set<String> set2 = map.get(user2);

        Set<String> mutual = new TreeSet<>(set1);
        mutual.retainAll(set2);

        return mutual;
    }

    // 🔥 Suggested Friends
    public static Set<String> suggestFriends(String user1, String user2,
                                             Map<String, Set<String>> map) {

        if (!map.containsKey(user1) || !map.containsKey(user2)) {
            return null;
        }

        Set<String> set1 = new HashSet<>(map.get(user1));
        Set<String> set2 = new HashSet<>(map.get(user2));

        // Union
        set1.addAll(set2);

        // Remove mutual
        Set<String> mutual = getMutualFriends(user1, user2, map);
        set1.removeAll(mutual);

        // Remove already friends
        set1.removeAll(map.get(user1));

        return set1;
    }
}