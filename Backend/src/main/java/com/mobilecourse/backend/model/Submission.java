package com.mobilecourse.backend.model;

import java.sql.Timestamp;

public class Submission {
    //投稿唯一id
    private int sid;
    //投稿者uid
    private int uid;
    //投稿类型, 0为文章, 1为视频
    private int type;
    //投稿板块pid
    private int pid;
    //投稿标题
    private String title;
    //投稿封面
    private String cover;
    //投稿简介
    private String introduction;
    //资源(html, 视频)
    private String resource;
    //投稿时间
    private Timestamp submissionTime;
    //观看次数
    private int watchTimes;

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public Timestamp getSubmissionTime() {
        return submissionTime;
    }

    public void setSubmissionTime(Timestamp submissionTime) {
        this.submissionTime = submissionTime;
    }

    public int getWatchTimes() {
        return watchTimes;
    }

    public void setWatchTimes(int watchTimes) {
        this.watchTimes = watchTimes;
    }
}
