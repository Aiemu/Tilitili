package com.mobilecourse.backend.model;

import java.sql.Timestamp;

public class Favorite {
    private Integer uid;
    private Integer sid;
    private Timestamp favoriteTime;

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getSid() {
        return sid;
    }

    public void setSid(Integer sid) {
        this.sid = sid;
    }

    public Timestamp getFavoriteTime() {
        return favoriteTime;
    }

    public void setFavoriteTime(Timestamp favoriteTime) {
        this.favoriteTime = favoriteTime;
    }
}
