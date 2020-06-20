package com.example.tilitili.data;

public class Plate {
    private int id;
    private String title;
    private String description;
    private String cover;

    public Plate(int id, String title, String description, String cover) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.cover = cover;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCover() {
        return cover;
    }
}
