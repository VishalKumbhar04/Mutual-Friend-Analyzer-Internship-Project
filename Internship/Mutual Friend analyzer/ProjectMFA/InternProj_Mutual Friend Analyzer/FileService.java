import java.io.*;
import java.util.*;

public class FileService {

    public static void saveData(Map<String, Set<String>> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data.ser"))) {
            oos.writeObject(data);
        } catch (Exception e) {
            System.out.println("Error saving data");
        }
    }

    public static Map<String, Set<String>> loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data.ser"))) {
            return (Map<String, Set<String>>) ois.readObject();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
}