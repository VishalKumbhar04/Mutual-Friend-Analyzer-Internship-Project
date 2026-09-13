package com.socialnetwork;

import com.socialnetwork.service.SocialNetworkService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SocialNetworkApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialNetworkApplication.class, args);
    }

    /**
     * Loads sample data on startup for testing/demo purposes.
     */
    @Bean
    CommandLineRunner loadSampleData(SocialNetworkService service) {
        return args -> {
            // Add sample users
            service.addUser("Alice");
            service.addUser("Bob");
            service.addUser("Charlie");
            service.addUser("Diana");
            service.addUser("Eve");
            service.addUser("Frank");

            // Add friendships (bidirectional)
            service.addFriend("Alice", "Bob");
            service.addFriend("Alice", "Charlie");
            service.addFriend("Alice", "Diana");
            service.addFriend("Bob", "Charlie");
            service.addFriend("Bob", "Eve");
            service.addFriend("Charlie", "Diana");
            service.addFriend("Charlie", "Frank");
            service.addFriend("Diana", "Eve");
            service.addFriend("Eve", "Frank");

            System.out.println("✅ Sample data loaded successfully!");
        };
    }
}
