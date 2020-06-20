package com.mobilecourse.backend.model;

import java.sql.Timestamp;

public class History {
    private int uid;
    private int sid;
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
