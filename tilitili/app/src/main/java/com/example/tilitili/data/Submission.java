package com.example.tilitili.data;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Submission implements Serializable {
    private int sid;
    private int type;
    private String plateTitle;
    private String title;
    private String cover;
    private String userAvatar;
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
    private int favoriteCount;
    private int isFavorite;

    public Submission(int sid, int type, String plateTitle, String title, String cover, String introduction, String resource, long submissionTime, long watchTimes, int likesCount, int isLike, int commentsCount, int uid, String userNickname, int following, int favoriteCount, int isFavorite) {
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
        this.favoriteCount = favoriteCount;
        this.isFavorite = isFavorite;
        this.userAvatar = "";
    }

    public Submission(int sid, int type, String plateTitle, String title, String cover, String introduction, String resource, long submissionTime, long watchTimes, int likesCount, int isLike, int commentsCount, int uid, String userNickname, int following, String userAvatar, int favoriteCount, int isFavorite) {
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
        this.userAvatar = userAvatar;
        this.favoriteCount = favoriteCount;
        this.isFavorite = isFavorite;
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
        return resource;
    }

    public String getSubmissionTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(new Date(this.submissionTime));
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

    public void setIsLike(int like) {
        isLike = like;
    }

    public void setLikesCount(int count) {
        likesCount = count;
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

    public String getUserAvatar() {
        return userAvatar;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public int getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }
}
