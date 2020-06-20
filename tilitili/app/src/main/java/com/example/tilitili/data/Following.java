package com.example.tilitili.data;

import com.example.tilitili.Config;

public class Following {
    private int userId;
    private String nickname;
    private String avatar;

    public Following(int id, String nickname, String avatar) {
        this.nickname = nickname;
        this.avatar = avatar;
        this.userId = id;
    }

    public int getUserId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAvatar() {
        return Config.getFullUrl(avatar);
    }
}

