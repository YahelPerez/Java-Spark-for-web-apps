package com.rafael.collectibles;

import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;
import static spark.Spark.*; // Import static methods from Spark

/**
 * Main class for the Collectibles Store API.
 * Sprint 1: Setup Maven, Spark, and User routes.
 */
public class Main {

    // 1. In-memory database simulation (a simple Map)
    private static Map<String, User> users = new HashMap<>();

    // 2. Gson instance for converting Java objects to JSON and vice-versa
    private static Gson gson = new Gson();

    public static void main(String[] args) {

        // Set the port the server will run on
        port(8080);

        // --- Route Definitions (Requirement 3b & 3c) ---

        // 'after' filter to ensure ALL responses
        // have the content-type "application/json"
        after((req, res) -> {
            res.type("application/json");
        });

        // GET /users — Retrieve the list of all users
        // gson::toJson is a method reference equivalent to (object) -> gson.toJson(object)
        get("/users", (req, res) -> {
            return users.values(); // Returns all values from the map
        }, gson::toJson);

        // GET /users/:id — Retrieve a user by the given ID
        get("/users/:id", (req, res) -> {
            String id = req.params(":id");
            User user = users.get(id);

            if (user != null) {
                return user; // Return the found user
            } else {
                res.status(404); // Not Found
                return new ErrorResponse("User not found");
            }
        }, gson::toJson);

        // POST /users/:id — Add a user
        post("/users/:id", (req, res) -> {
            String id = req.params(":id");

            if (users.containsKey(id)) {
                res.status(409); // Conflict (Resource already exists)
                return new ErrorResponse("User ID already exists");
            }

            // Convert the JSON body of the request to a User object
            User newUser = gson.fromJson(req.body(), User.class);
            newUser.setId(id); // Ensure the ID is the one from the URL

            users.put(id, newUser); // Add the new user to the map

            res.status(201); // Created
            return newUser;
        }, gson::toJson);

        // PUT /users/:id — Edit a specific user
        put("/users/:id", (req, res) -> {
            String id = req.params(":id");

            if (!users.containsKey(id)) {
                res.status(404); // Not Found
                return new ErrorResponse("User not found");
            }

            User updatedUser = gson.fromJson(req.body(), User.class);
            updatedUser.setId(id); // Force the ID from the URL

            users.put(id, updatedUser); // Overwrites the existing user

            return updatedUser;
        }, gson::toJson);

        // DELETE /users/:id — Delete a specific user
        delete("/users/:id", (req, res) -> {
            String id = req.params(":id");
            User removedUser = users.remove(id); // Attempt to remove the user

            if (removedUser != null) {
                return new ErrorResponse("User deleted successfully");
            } else {
                res.status(404); // Not Found
                return new ErrorResponse("User not found");
            }
        }, gson::toJson);

        // OPTIONS /users/:id — Check whether a user with the given ID exists
        options("/users/:id", (req, res) -> {
            if (users.containsKey(req.params(":id"))) {
                return new ErrorResponse("User exists");
            } else {
                res.status(404);
                return new ErrorResponse("User not found");
            }
        }, gson::toJson);

        System.out.println("Collectibles API started at http://localhost:8080");
        System.out.println("User endpoints ready at /users");
    }

    // --- Model and Response Classes ---
    // (Placing them here as 'static' classes is simpler for this example)

    /**
     * Simple Model for a User
     */
    static class User {
        private String id;
        private String name;
        private String email;

        // Constructor
        public User(String name, String email) {
            this.name = name;
            this.email = email;
        }

        // Getters and Setters are necessary for Gson to work
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    /**
     * Simple class for standard error responses
     */
    static class ErrorResponse {
        private String message;
        public ErrorResponse(String message) {
            this.message = message;
        }
    }
}