package com.example.tilitili.data;

public class Plate {
    private int pid;
    private String title;
    private String description;
    private String cover;

    public Plate(int pid, String title, String description, String cover) {
        this.pid = pid;
        this.title = title;
        this.description = description;
        this.cover = cover;
    }

    public int getPid() {
        return pid;
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
