package com.mobilecourse.backend.model;

import java.sql.Timestamp;

public class Message {
    //消息唯一id
    private Integer mid;
    //消息来源uid
    private Integer srcUid;
    //消息目标uid
    private Integer destUid;
    //消息种类
    private Integer type;
    //消息内容
    private String content;
    //发消息的时间
    private Timestamp messageTime;

    public Integer getMid() {
        return mid;
    }

    public void setMid(Integer mid) {
        this.mid = mid;
    }

    public Integer getSrcUid() {
        return srcUid;
    }

    public void setSrcUid(Integer srcUid) {
        this.srcUid = srcUid;
    }

    public Integer getDestUid() {
        return destUid;
    }

    public void setDestUid(Integer destUid) {
        this.destUid = destUid;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(Timestamp messageTime) {
        this.messageTime = messageTime;
    }
}
