package com.mobilecourse.backend.model;

public class Plate {
    //板块id
    private int pid;
    //板块类型, 0代表公共板块(谁都可以投稿), 1代表权限板块(需要有权限)
    private int type;
    //板块标题
    private String title;
    //板块描述
    private String description;
    //板块封面
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
