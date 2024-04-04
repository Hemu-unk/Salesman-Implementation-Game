package com.example.phase1.Main;

public class Treasure {
    private final String name;
    private final int value;
    public static final Treasure[] treasures = {
            new Treasure("Diamond Ring", 100),
            new Treasure("Jewel-encrusted Sword", 150),
            new Treasure("Golden Goblet", 200),
            new Treasure("Crystal Goblets", 120),
            new Treasure("Wooden Bow", 80),
            new Treasure("Paladin’s Shield", 180),
            new Treasure("Golden Key", 90),
            new Treasure("Dragon’s Scroll", 250)
    };


    public Treasure(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }
}
