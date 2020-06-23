package com.mobilecourse.backend.model;

import java.sql.Timestamp;

public class History {
    //观看者uid
    private int uid;
    //观看的投稿sid
    private int sid;
    //观看时间
    private Timestamp watchTime;

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

    public Timestamp getWatchTime() {
        return watchTime;
    }

    public void setWatchTime(Timestamp watchTime) {
        this.watchTime = watchTime;
    }
}
