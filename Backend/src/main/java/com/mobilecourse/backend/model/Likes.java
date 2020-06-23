package com.mobilecourse.backend.model;

public class Likes {
    //点赞用户uid
    private int uid;
    //点赞稿件sid
    private int sid;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }
}
