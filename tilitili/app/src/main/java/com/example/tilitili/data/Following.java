package com.example.tilitili.data;

import com.example.tilitili.Config;

public class Following {
    private int userId;
    private String nickname;
    private String avatar;
    private int isFollowing;

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

    public int getIsFollowing() {
        return isFollowing;
    }

    public void setIsFollowing(int isFollowing) {
        this.isFollowing = isFollowing;
    }
}
