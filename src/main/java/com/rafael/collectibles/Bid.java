package com.rafael.collectibles;

import java.time.LocalDateTime;

/**
 * Represents a bid made on an item.
 */
public class Bid {
    private String id;
    private String itemId;
    private String bidderName;
    private double amount;
    private LocalDateTime bidTime;

    public Bid(String id, String itemId, String bidderName, double amount) {
        this.id = id;
        this.itemId = itemId;
        this.bidderName = bidderName;
        this.amount = amount;
        this.bidTime = LocalDateTime.now();
    }

    // Getters and setters
    public String getId() { return id; }
    public String getItemId() { return itemId; }
    public String getBidderName() { return bidderName; }
    public double getAmount() { return amount; }
    public LocalDateTime getBidTime() { return bidTime; }

    // Helper method for template display
    public String getTimeAgo() {
        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(bidTime, now).toMinutes();
        
        if (minutes < 1) return "just now";
        if (minutes < 60) return minutes + " minutes ago";
        
        long hours = minutes / 60;
        if (hours < 24) return hours + " hours ago";
        
        long days = hours / 24;
        return days + " days ago";
    }
}