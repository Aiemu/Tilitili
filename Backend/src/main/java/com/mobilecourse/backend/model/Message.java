package com.mobilecourse.backend.model;

import java.sql.Timestamp;

public class Message {
    private Integer mid;
    private Integer srcUid;
    private Integer destUid;
    private Integer type;
    private String content;
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
