package com.example.tilitili.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Message {
    @PrimaryKey()
    public int id;

    @ColumnInfo(name = "uid")
    public int uid;

    @ColumnInfo(name = "content")
    public String content;

    @ColumnInfo(name = "type")
    public int type;

    @ColumnInfo(name = "nickname")
    public String nickname;

    @ColumnInfo(name = "avatar")
    public String avatar;

    public Message(int id, int uid, String content, int type, String nickname, String avatar) {
        this.id = id;
        this.uid = uid;
        this.content = content;
        this.type = type;
        this.nickname = nickname;
        this.avatar = avatar;
    }

    public int getId() {
        return id;
    }

    public int getUid() {
        return uid;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAvatar() {
        return avatar;
    }
}
