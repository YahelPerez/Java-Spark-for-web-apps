package com.rafael.collectibles;

import com.google.gson.Gson;
import com.rafael.collectibles.exception.GlobalExceptionHandler;
import com.rafael.collectibles.exception.ResourceNotFoundException;
import com.rafael.collectibles.exception.ValidationException;
import spark.template.mustache.MustacheTemplateEngine;
import spark.ModelAndView;
import java.util.HashMap;
import java.util.Map;
import static spark.Spark.*; // Import static methods from Spark

/**
 * Main class for the Collectibles Store API.
 * Sprint 1: Setup Maven, Spark, and User routes.
 * Sprint 2: Add Mustache templates, exception handling, and Item routes.
 */
public class Main {

    // --- SPRINT 1: User database ---
    private static Map<String, User> users = new HashMap<>();

    // --- SPRINT 2: Item database ---
    private static Map<String, Item> items = new HashMap<>();

    // JSON Converter
    private static Gson gson = new Gson();

    public static void main(String[] args) {

        // Set the port
        port(8080);

        // --- SPRINT 2: Serve static files (CSS/JS) ---
        // Tells Spark to serve files from the 'src/main/resources/public' folder
        staticFiles.location("/public");

        // --- SPRINT 2: Populate sample data ---
        populateItems();

        // Configure global exception handling
        GlobalExceptionHandler.configure();

        // --- SPRINT 1: User API Routes (JSON) ---
        // (This code block is the same as Sprint 1)
        path("/users", () -> {
            // Filter to ensure all /users responses are JSON
            after("/*", (req, res) -> res.type("application/json"));

            // GET /users — Retrieve the list of all users
            get("", (req, res) -> users.values(), gson::toJson);

            // GET /users/:id — Retrieve a user by the given ID
            get("/:id", (req, res) -> {
                User user = users.get(req.params(":id"));
                if (user == null) {
                    throw new ResourceNotFoundException("User", req.params(":id"));
                }
                return user;
            }, gson::toJson);

            // POST /users/:id — Add a user
            post("/:id", (req, res) -> {
                String id = req.params(":id");
                if (users.containsKey(id)) {
                    throw new ValidationException("id", "User ID already exists");
                }
                User newUser = gson.fromJson(req.body(), User.class);
                newUser.setId(id);
                users.put(id, newUser);
                res.status(201);
                return newUser;
            }, gson::toJson);

            // PUT /users/:id — Edit a specific user
            put("/:id", (req, res) -> {
                String id = req.params(":id");
                if (!users.containsKey(id)) {
                    throw new ResourceNotFoundException("User", id);
                }
                User updatedUser = gson.fromJson(req.body(), User.class);
                updatedUser.setId(id);
                users.put(id, updatedUser);
                return updatedUser;
            }, gson::toJson);

            // DELETE /users/:id — Delete a specific user
            delete("/:id", (req, res) -> {
                User removedUser = users.remove(req.params(":id"));
                if (removedUser == null) {
                    throw new ResourceNotFoundException("User", req.params(":id"));
                }
                res.status(200);
                return new SuccessResponse("User deleted successfully");
            }, gson::toJson);

            // OPTIONS /users/:id — Check whether a user with the given ID exists
            options("/:id", (req, res) -> {
                if (!users.containsKey(req.params(":id"))) {
                    throw new ResourceNotFoundException("User", req.params(":id"));
                }
                return new SuccessResponse("User exists");
            }, gson::toJson);
        });

        // --- SPRINT 2: View Routes (HTML/Mustache) ---

        // GET /items — Show the list of items (Requirement 2)
        get("/items", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("title", "Available Collectibles");
            model.put("items", items.values());
            model.put("content", new MustacheTemplateEngine().render(
                new ModelAndView(model, "list.mustache")
            ));
            return new ModelAndView(model, "layout.mustache");
        }, new MustacheTemplateEngine());

        // GET /items/:id — Show the detail for one item (Requirement 2)
        get("/items/:id", (req, res) -> {
            Item item = items.get(req.params(":id"));
            if (item == null) {
                throw new ResourceNotFoundException("Item", req.params(":id"));
            }
            Map<String, Object> model = new HashMap<>();
            model.put("title", item.getName());
            model.put("item", item);
            model.put("content", new MustacheTemplateEngine().render(
                new ModelAndView(model, "detail.mustache")
            ));
            return new ModelAndView(model, "layout.mustache");
        }, new MustacheTemplateEngine());

        // POST /items/:id/bid — Receive the bidding form (Requirement 3)
        post("/items/:id/bid", (req, res) -> {
            String id = req.params(":id");
            Item item = items.get(id);
            if (item == null) {
                throw new ResourceNotFoundException("Item", id);
            }

            // Get and validate form inputs
            String bidAmount = req.queryParams("bidAmount");
            String bidderName = req.queryParams("bidderName");

            // Validate bidder name
            if (bidderName == null || bidderName.trim().isEmpty()) {
                throw new ValidationException("bidderName", "Bidder name is required");
            }

            // Validate bid amount
            double amount;
            try {
                amount = Double.parseDouble(bidAmount);
                if (amount <= 0) {
                    throw new ValidationException("bidAmount", "Bid amount must be positive");
                }
                if (amount <= item.getCurrentPrice()) {
                    throw new ValidationException("bidAmount", 
                        String.format("Bid must be higher than current price ($%.2f)", item.getCurrentPrice()));
                }
            } catch (NumberFormatException e) {
                throw new ValidationException("bidAmount", "Invalid bid amount format");
            }

            // Create and add the new bid
            String bidId = "bid" + System.currentTimeMillis(); // Simple unique ID generation
            Bid newBid = new Bid(bidId, id, bidderName, amount);
            item.addBid(newBid);

            // Redirect back to the item page
            res.redirect("/items/" + id);
            return null;
        });

        System.out.println("Collectibles API (Sprint 2) started at http://localhost:8080");
    }

    // --- SPRINT 2: Helper method to populate data ---
    private static void populateItems() {
        Item item1 = new Item("comic001", "Action Comics #1", "The first appearance of Superman from 1938. Certified CGC 8.0 grade. This is one of the most valuable and sought-after comic books in existence.", 2500000.00);
        Item item2 = new Item("stamp001", "Penny Black", "The world's first adhesive postage stamp, issued in the UK in 1840. Well preserved with clear margins.", 3000.00);
        Item item3 = new Item("coin001", "1913 Liberty Head Nickel", "One of only five known specimens of this legendary US coin. Professional certified and graded.", 3700000.00);

        // Add some sample bids to demonstrate functionality
        try {
            item1.addBid(new Bid("bid1", item1.getId(), "John Smith", 2600000.00));
            item1.addBid(new Bid("bid2", item1.getId(), "Jane Doe", 2650000.00));
            
            item2.addBid(new Bid("bid3", item2.getId(), "Alice Brown", 3200.00));
            
            item3.addBid(new Bid("bid4", item3.getId(), "Bob Wilson", 3750000.00));
            item3.addBid(new Bid("bid5", item3.getId(), "Carol White", 3800000.00));
            item3.addBid(new Bid("bid6", item3.getId(), "David Lee", 3850000.00));
        } catch (Exception e) {
            System.err.println("Error adding sample bids: " + e.getMessage());
        }

        items.put(item1.getId(), item1);
        items.put(item2.getId(), item2);
        items.put(item3.getId(), item3);
    }

    // Models and simple response POJOs were extracted to separate files:
    // - com.rafael.collectibles.User
    // - com.rafael.collectibles.Item
    // - com.rafael.collectibles.ErrorResponse
    // - com.rafael.collectibles.SuccessResponse
}