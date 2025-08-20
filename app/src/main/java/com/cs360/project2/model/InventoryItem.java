package com.cs360.project2.model;
/*
 * UI model for a single inventory row.
 * - Immutable name & lastUpdated (set at insert/update time).
 * - Mutable quantity (changed in adapter with optimistic UI).
 */
public class InventoryItem {

    /* Name shown in grid row. */
    private final String name;

    /* Count in stock (may change during session). */
    private int quantity;

    /* Last user edit date in MM/dd/yyyy format. */
    private final String lastUpdated;

    public InventoryItem(String name, int quantity, String lastUpdated) {
        this.name = name;
        this.quantity = quantity;
        this.lastUpdated = lastUpdated;
    }

    /* Getters */
    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    /* Setters */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
