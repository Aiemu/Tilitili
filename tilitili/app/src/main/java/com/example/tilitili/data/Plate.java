package com.example.tilitili.data;

public class Plate {
    private int id;
    private String title;
    private int owner;
    private long startTime;
    private String description;

    public Plate(int id, String title, int owner, long startTime, String description) {
        this.id = id;
        this.title = title;
        this.owner = owner;
        this.startTime = startTime;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getOwner() {
        return owner;
    }

    public long getStartTime() {
        return startTime;
    }

    public String getDescription() {
        return description;
    }
}
