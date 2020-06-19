package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.Follow;

import java.util.List;

public interface FollowDao {
    Follow getFollow(Integer followerUid, Integer followedUid);
    List<Integer> getFollowers(Integer followedUid); //获取关注这个人的所有人
    List<Integer> getFolloweds(Integer followerUid); //获取所有关注的人
    void putFollow(Integer followerUid, Integer followedUid);
    void deleteFollow(Integer followerUid, Integer followedUid);
}
