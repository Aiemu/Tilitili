package com.mobilecourse.backend.model;

import java.sql.Timestamp;

public class Comment {
    //评论id
    private int cid;
    //评论所在的投稿sid
    private int sid;
    //评论者uid
    private int uid;
    //评论内容
    private String content;
    //点赞数
    private int like;
    //评论时间
    private Timestamp commentTime;

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public Timestamp getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(Timestamp commentTime) {
        this.commentTime = commentTime;
    }
}
