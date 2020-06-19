package com.mobilecourse.backend.model;

public class Follow {
    private int followerUid;
    private int followedUid;

    public int getFollowerUid() {
        return followerUid;
    }

    public void setFollowerUid(int followerUid) {
        this.followerUid = followerUid;
    }

    public int getFollowedUid() {
        return followedUid;
    }

    public void setFollowedUid(int followedUid) {
        this.followedUid = followedUid;
    }
}
