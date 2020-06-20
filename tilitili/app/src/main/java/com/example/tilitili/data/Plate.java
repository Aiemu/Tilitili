package com.example.tilitili.data;

public class Plate {
    private int sid;
    private String title;
    private String description;
    private String cover;

    public Plate(int sid, String title, String description, String cover) {
        this.sid = sid;
        this.title = title;
        this.description = description;
        this.cover = cover;
    }

    public int getSid() {
        return sid;
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
