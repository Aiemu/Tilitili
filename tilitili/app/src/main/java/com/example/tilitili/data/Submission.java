package com.example.tilitili.data;

import android.annotation.SuppressLint;

import com.example.tilitili.Config;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Submission {
    private int sid;
    private int type;
    private String plateTitle;
    private String title;
    private String cover;
    private String introduction;
    private String resource;
    private long submissionTime;
    private long watchTimes;
    private int likesCount;
    private int isLike;
    private int commentsCount;
    private int uid;
    private String userNickname;
    private int following;
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public Submission(int sid, int type, String plateTitle, String title, String cover, String introduction, String resource, long submissionTime, long watchTimes, int likesCount, int isLike, int commentsCount, int uid, String userNickname, int following) {
        this.sid = sid;
        this.type = type;
        this.plateTitle = plateTitle;
        this.title = title;
        this.cover = cover;
        this.introduction = introduction;
        this.resource = resource;
        this.submissionTime = submissionTime;
        this.watchTimes = watchTimes;
        this.likesCount = likesCount;
        this.isLike = isLike;
        this.commentsCount = commentsCount;
        this.uid = uid;
        this.userNickname = userNickname;
        this.following = following;
    }

    public int getSid() {
        return sid;
    }

    public int getType() {
        return type;
    }

    public String getPlateTitle() {
        return plateTitle;
    }

    public String getTitle() {
        return title;
    }

    public String getCover() {
        return cover;
    }

    public String getIntroduction() {
        return introduction;
    }

    public String getResource() {
        return Config.getFullUrl(resource);
    }

    public String getSubmissionTime() {
        return sdf.format(new Date(this.submissionTime * 1000L));
    }

    public long getWatchTimes() {
        return watchTimes;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public int getIsLike() {
        return isLike;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public int getUid() {
        return uid;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public int getFollowing() {
        return following;
    }
}
