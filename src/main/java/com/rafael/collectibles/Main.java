package com.rafael.collectibles;

import com.google.gson.Gson;
import com.rafael.collectibles.exception.GlobalExceptionHandler;
import com.rafael.collectibles.exception.ResourceNotFoundException;
import com.rafael.collectibles.exception.ValidationException;
import spark.template.mustache.MustacheTemplateEngine;
import spark.ModelAndView;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.stream.Collectors;
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
        // Initialize Spark configuration
        port(8080);
        staticFiles.location("/public");

        // Configure WebSocket
        webSocket("/websocket/prices", PriceUpdateWebSocket.class);

        // Configure global exception handling first
        GlobalExceptionHandler.configure();

        // --- SPRINT 2: Populate sample data ---
        populateItems();

        // API route para actualizar precios
        path("/api", () -> {
            after("/*", (req, res) -> res.type("application/json"));
            
            put("/items/:id/price", (req, res) -> {
                String id = req.params(":id");
                Item item = items.get(id);
                if (item == null) {
                    throw new ResourceNotFoundException("Item", id);
                }
                
                try {
                    // Parse el nuevo precio del body JSON
                    PriceUpdate priceUpdate = gson.fromJson(req.body(), PriceUpdate.class);
                    double newPrice = priceUpdate.price;
                    
                    if (newPrice <= 0) {
                        throw new ValidationException("price", "Price must be greater than 0");
                    }
                    
                    item.setStartingPrice(newPrice);
                    return new SuccessResponse("Price updated successfully");
                } catch (Exception e) {
                    throw new ValidationException("price", "Invalid price format: " + e.getMessage());
                }
            }, gson::toJson);
        });

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

        // GET /items — Show the list of items with optional price filters
        get("/items", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("title", "Available Collectibles");
            
            // Get price range parameters
            String minPriceStr = req.queryParams("minPrice");
            String maxPriceStr = req.queryParams("maxPrice");
            
            // Parse and validate price range
            Double minPrice = minPriceStr != null && !minPriceStr.isEmpty() ? Double.parseDouble(minPriceStr) : null;
            Double maxPrice = maxPriceStr != null && !maxPriceStr.isEmpty() ? Double.parseDouble(maxPriceStr) : null;
            
            // Filter items based on price range
            Collection<Item> filteredItems = items.values().stream()
                .filter(item -> {
                    double currentPrice = item.getCurrentPrice();
                    boolean matchesMin = minPrice == null || currentPrice >= minPrice;
                    boolean matchesMax = maxPrice == null || currentPrice <= maxPrice;
                    return matchesMin && matchesMax;
                })
                .collect(Collectors.toList());
            
            // Add all model attributes
            model.put("items", filteredItems);
            model.put("minPrice", minPrice != null ? String.format("%.2f", minPrice) : "");
            model.put("maxPrice", maxPrice != null ? String.format("%.2f", maxPrice) : "");
            
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

        // Initialize Spark after all routes are configured
        init();
        
        System.out.println("Collectibles API (Sprint 2) started at http://localhost:8080");
        System.out.println("- View all items at: http://localhost:8080/items");
        System.out.println("- WebSocket endpoint at: ws://localhost:8080/websocket/prices");
        System.out.println("- REST API endpoints:");
        System.out.println("  * GET /users");
        System.out.println("  * GET/POST/PUT/DELETE /users/:id");
        System.out.println("  * PUT /api/items/:id/price");
    }

    // --- SPRINT 2: Helper method to populate data ---
    private static void populateItems() {
        // Artículos de rango económico (menos de $500)
        Item item5 = new Item("item5", "Jersey firmado por Snoop Dogg", "Un jersey autografiado por el legendario rapero Snoop Dogg.", 355.67);
        Item item7 = new Item("item7", "Guitarra autografiada por Coldplay", "Una guitarra eléctrica autografiada por la popular banda británica Coldplay, un día antes de su concierto en Monterrey en 2022.", 458.91);

        // Artículos de rango medio ($500-$700)
        Item item3 = new Item("item3", "Chamarra de Bad Bunny", "Una chamarra de la marca favorita de Bad Bunny, autografiada por el propio artista.", 521.89);
        Item item1 = new Item("item1", "Gorra autografiada por Peso Pluma", "Una gorra autografiada por el famoso Peso Pluma.", 621.34);
        Item item6 = new Item("item6", "Prenda de Cardi B autografiada", "Un crop-top usado y autografiado por la famosa rapera Cardi B. en su última visita a México", 674.23);

        // Artículos premium (más de $700)
        Item item2 = new Item("item2", "Casco autografiado por Rosalía", "Un casco autografiado por la famosa cantante Rosalía, una verdadera MOTOMAMI!", 734.57);
        Item item4 = new Item("item4", "Guitarra de Fernando Delgadillo", "Una guitarra acústica de alta calidad utilizada por el famoso cantautor Fernando Delgadillo.", 823.12);

        // Primero agregamos todos los items a la colección
        items.put(item1.getId(), item1);
        items.put(item2.getId(), item2);
        items.put(item3.getId(), item3);
        items.put(item4.getId(), item4);
        items.put(item5.getId(), item5);
        items.put(item6.getId(), item6);
        items.put(item7.getId(), item7);

        // Luego agregamos algunas ofertas (manteniendo los precios dentro de sus rangos respectivos)
        try {
            // Ofertas para artículos de rango medio
            item1.addBid(new Bid("bid1", item1.getId(), "Juan Pérez", 645.00));
            item1.addBid(new Bid("bid2", item1.getId(), "María García", 660.00));
            
            item3.addBid(new Bid("bid3", item3.getId(), "Ana Martínez", 550.00));
            item3.addBid(new Bid("bid4", item3.getId(), "Carlos López", 575.00));
            
            item6.addBid(new Bid("bid5", item6.getId(), "Roberto Díaz", 690.00));
            
            // Ofertas para artículos premium
            item2.addBid(new Bid("bid6", item2.getId(), "Laura Sánchez", 760.00));
            item4.addBid(new Bid("bid7", item4.getId(), "Miguel Ángel", 850.00));
        } catch (Exception e) {
            System.err.println("Error adding sample bids: " + e.getMessage());
        }
    }

    // Models and simple response POJOs were extracted to separate files:
    // - com.rafael.collectibles.User
    // - com.rafael.collectibles.Item
    // - com.rafael.collectibles.ErrorResponse
    // - com.rafael.collectibles.SuccessResponse
}