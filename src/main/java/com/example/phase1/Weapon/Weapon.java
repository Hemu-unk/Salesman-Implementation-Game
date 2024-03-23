package com.example.phase1.Weapon;

public class Weapon {
    private final String name;
    private int strength; // Change to non-final

    private final int price;

    public Weapon(String name, int strength, int price) {
        this.name = name;
        this.strength = strength; // Initialize strength
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) { // Add setter method for strength
        this.strength = strength;
    }

    public int getPrice() {
        return price;
    }
}
