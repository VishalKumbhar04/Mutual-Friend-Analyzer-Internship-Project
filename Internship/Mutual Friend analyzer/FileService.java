import java.io.*;
import java.util.*;

/**
 * FileService.java
 * — saveData() : serializes userFriendsMap to data.ser
 * — loadData() : deserializes from data.ser (returns empty map if not found)
 */
public class FileService {

    public static void saveData(Map<String, Set<String>> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream("data.ser"))) {
            oos.writeObject(data);
        } catch (Exception e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Set<String>> loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream("data.ser"))) {
            return (Map<String, Set<String>>) ois.readObject();
        } catch (Exception e) {
            return new HashMap<>(); // fresh start if no file found
        }
    }
}
