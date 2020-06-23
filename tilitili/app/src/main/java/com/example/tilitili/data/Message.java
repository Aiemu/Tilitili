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

    @ColumnInfo(name = "receiver")
    public int receiver;

    @ColumnInfo(name = "content")
    public String content;

    @ColumnInfo(name = "nickname")
    public String nickname;

    @ColumnInfo(name = "avatar")
    public String avatar;

    @ColumnInfo(name = "messageTime")
    public long messageTime;

    @ColumnInfo(name = "isRead")
    public int isRead;

    public Message(int id, int uid, int receiver, String content, String nickname, String avatar, long messageTime, int isRead) {
        this.id = id;
        this.uid = uid;
        this.receiver = receiver;
        this.content = content;
        this.nickname = nickname;
        this.avatar = avatar;
        this.messageTime = messageTime;
        this.isRead = isRead;
    }

    public int getId() {
        return id;
    }

    public int getUid() {
        return uid;
    }

    public int getReceiver() {
        return receiver;
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

    public int getIsRead() {
        return isRead;
    }

    public long getMessageTime() {
        return messageTime;
    }
}
