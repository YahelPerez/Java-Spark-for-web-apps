package com.rafael.collectibles;

import com.rafael.collectibles.exception.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

/**
 * Represents an item in the collectibles store.
 */
public class Item {
    private String id;
    private String name;
    private String description;
    private double startingPrice;
    private LocalDateTime createdAt;
    private List<Bid> bids;
    private boolean active;

    public Item(String id, String name, String description, double startingPrice) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startingPrice = startingPrice;
        this.createdAt = LocalDateTime.now();
        this.bids = new ArrayList<>();
        this.active = true;
    }

    // Getters needed for Mustache templates
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getStartingPrice() { return startingPrice; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isActive() { return active; }
    
    // Bid-related methods
    public double getCurrentPrice() {
        return bids.isEmpty() ? startingPrice : getHighestBid().getAmount();
    }

    public double getMinBid() {
        return getCurrentPrice() + 0.01; // Mínimo incremento de $0.01
    }

    public int getBidCount() {
        return bids.size();
    }

    public List<Bid> getBids() {
        return Collections.unmodifiableList(bids);
    }

    public List<Bid> getRecentBids() {
        return bids.stream()
                  .sorted(Comparator.comparing(Bid::getBidTime).reversed())
                  .limit(5)
                  .toList();
    }

    public Bid getHighestBid() {
        return bids.stream()
                  .max(Comparator.comparingDouble(Bid::getAmount))
                  .orElse(null);
    }

    public void addBid(Bid bid) {
        if (!active) {
            throw new IllegalStateException("This item is no longer accepting bids");
        }
        if (bid.getAmount() <= getCurrentPrice()) {
            throw new ValidationException("bidAmount", "Bid amount must be higher than current price");
        }
        bids.add(bid);
    }

    // Helper methods for templates
    public String getTimeLeft() {
        // Por ahora, un tiempo fijo de 7 días desde la creación
        LocalDateTime endTime = createdAt.plusDays(7);
        LocalDateTime now = LocalDateTime.now();
        
        if (now.isAfter(endTime)) {
            this.active = false;
            return "Ended";
        }

        long days = java.time.Duration.between(now, endTime).toDays();
        long hours = java.time.Duration.between(now, endTime).toHours() % 24;
        
        if (days > 0) {
            return days + "d " + hours + "h left";
        } else if (hours > 0) {
            return hours + "h left";
        } else {
            long minutes = java.time.Duration.between(now, endTime).toMinutes() % 60;
            return minutes + "m left";
        }
    }

    public boolean isNew() {
        return LocalDateTime.now().minusDays(1).isBefore(createdAt);
    }
}
