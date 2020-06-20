package com.example.tilitili.data;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Comment {
    private String content;
    private String nickname;
    private String avatar;
    private long commentTime;

    public Comment(String content, String nickname, String avatar, long commentTime) {
        this.content = content;
        this.nickname = nickname;
        this.avatar = avatar;
        this.commentTime = commentTime;
    }

    public String getContent() {
        return content;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getCommentTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(new Date(this.commentTime * 1000L));
    }
}
