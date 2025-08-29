package com.example.myapplication5;

public class Food {
    private String name;
    private String description;
    private double price;
    private String emoji;

    public Food(String name, String description, double price, String emoji) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.emoji = emoji;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getEmoji() {
        return emoji;
    }
}