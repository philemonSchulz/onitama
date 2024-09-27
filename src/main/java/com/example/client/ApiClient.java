package com.example.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class ApiClient {
    private final RestTemplate restTemplate;

    public ApiClient() {
        this.restTemplate = new RestTemplate();
    }

    public User getUser(String id) {
        String url = "http://localhost:8080/api/users/" + id;
        ResponseEntity<User> response = restTemplate.getForEntity(url, User.class);
        return response.getBody();
    }

    public User createUser(User user) {
        String url = "http://localhost:8080/api/users";
        return restTemplate.postForObject(url, user, User.class);
    }

    public static void main(String[] args) {
        ApiClient client = new ApiClient();

        // Creating a new user
        User newUser = new User("1", "John Doe", "john.doe@example.com");
        User createdUser = client.createUser(newUser);
        System.out.println("Created User: " + createdUser.getName());

        // Fetching a user
        User fetchedUser = client.getUser("1");
        System.out.println("Fetched User: " + fetchedUser.getName());
    }
}
