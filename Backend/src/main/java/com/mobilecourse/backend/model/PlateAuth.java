package com.mobilecourse.backend.model;

public class PlateAuth {
    //板块pid
    private int pid;
    //可以在该板块投稿的用户uid
    private int uid;

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
