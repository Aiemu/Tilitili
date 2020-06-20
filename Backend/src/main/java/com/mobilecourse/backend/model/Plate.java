package com.mobilecourse.backend.model;

public class Plate {
    private int pid;
    private int type; //0代表公共板块, 1代表权限板块.
    private String title;
    private String description;
    private String cover;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}
